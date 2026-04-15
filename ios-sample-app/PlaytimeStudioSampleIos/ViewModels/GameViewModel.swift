import Foundation
import PlaytimeMonetize
import IdentifiedCollections

@MainActor
final class GameViewModel: ObservableObject {
    @Published var catalogGames: IdentifiedArrayOf<Game> = []
    @Published var loadingCatalog: Bool = true
    @Published var loadedCatalog: Bool = false
    @Published var inProgressGames: IdentifiedArrayOf<Game> = []
    @Published var loadingInProgress: Bool = true
    @Published var loadedInProgress: Bool = false
    // Used as a single source of truth for saved ClickedCampaigns
    var clickedCampaigns: ClickedCampaigns = ClickedCampaigns(campaigns: [])
    // Used to store clicke catalog games and put them to "In Progress"
    var clickedCatalogGames: IdentifiedArrayOf<Game> = []
    var didSetOptions: Bool = false
    
    func saveClickedCampaigns() {
        do {
            let jsonData = try JSONEncoder().encode(clickedCampaigns)
            let jsonString = String(data: jsonData, encoding: .utf8) ?? ""
            PreferencesRepository.setClickedCampaigns(value: jsonString)
        } catch {
            print("Error saving clicked campaigns: \(error)")
        }
    }
    
    func addClickedCampaign(game: Game) async {
        if clickedCampaigns.campaigns[id: game.id] == nil {
            // Remove it from catalog
            catalogGames = catalogGames.filter { $0.id != game.id }
            
            // Add to clickedCampaigns
            clickedCampaigns.campaigns.append(
                ClickedCampaign(
                    id: game.id,
                    clickedAt: Date().ISO8601Format()
                )
            )
            
            // Add to installed campaigns
            var inProgressGame = game
            inProgressGame.clickedAt = Date().ISO8601Format()
            inProgressGame.clickedDuration = 0
            
            if !loadedInProgress {
                await fetchInstalledCampaigns()
            }
            
            clickedCatalogGames.append(inProgressGame)
            inProgressGames.insert(inProgressGame, at: 0)
            saveClickedCampaigns()
        }
    }
    
    func checkGameInstallation(game: Game, minutesPassed: Int) async {
        // If postback is timed out, change status of the game in progress
        if minutesPassed >= Game.Constants.postbackTimeout {
            let clickedCampaign = clickedCampaigns.campaigns[id: game.id]
            if var inProgressGame = inProgressGames[id: game.id] {
                inProgressGame.clickedDuration = 20
                inProgressGame.clickedAt = clickedCampaign?.clickedAt
            }
            return
        }
        
        do {
            let installedGames = try await Game.getInstalledCampaigns()
            let installedGame = installedGames[id: game.id]
            
            if let installedGame {
                // Add installed game to inProgress
                inProgressGames.removeAll { $0.id == game.id }
                inProgressGames.append(installedGame)
                
                // Remove from clickedCampaigns
                clickedCampaigns.campaigns.removeAll { $0.id == game.id }
                saveClickedCampaigns()
                return
            }
        } catch {
            print("Error fetching campaigns: \(error)")
        }
    }
    
    func onClickPlayGame(_ game: Game) async {
        guard let campaign = game.campaign else {
            return
        }
        
        if let _ = campaign.installedAt {
            do {
                try await PlaytimeStudio.openInstalledCampaign(campaign: campaign)
            } catch {
                print("Error opening installed game: \(error)")
            }
            return
        }
        
        do {
            try await PlaytimeStudio.openInStore(campaign: campaign)
        } catch {
            print("Error opening store: \(error)")
        }
        
        await addClickedCampaign(game: game)
        
        Task.detached(priority: .background) {
            await self.checkGameInstallation(game: game, minutesPassed: 0)
        }
    }

    func fetchClickedCampaigns() {
        do {
            let jsonClickedCampaigns = PreferencesRepository.getClickedCampaigns()
            let clickedCampaigns = try JSONHelper.decodeClickedCampaigns(jsonData: jsonClickedCampaigns)
            self.clickedCampaigns = clickedCampaigns
        } catch {
            print("Error getting clicked campaigns: \(error)")
        }
    }
    
    func startCheckClickedGames() {
        Timer.scheduledTimer(withTimeInterval: Game.Constants.timerInterval, repeats: true) { [] _ in
            for clickedCatalogGame in self.clickedCatalogGames {
                let clickedAtDate = Date.isoStringToDate(clickedCatalogGame.clickedAt ?? "") ?? Date()
                let minutesPassed = Date.minutesBetweenDates(clickedAtDate, Date())
                print("game \(clickedCatalogGame.name) : \(minutesPassed)")
                
                if minutesPassed <= Game.Constants.postbackTimeout {
                    Task.detached(priority: .background) {
                        await self.checkGameInstallation(
                            game: clickedCatalogGame,
                            minutesPassed: minutesPassed
                        )
                    }
                }
            }
        }
    }
    
    func filterCatalogCampaigns(_ campaign: Game, checkValue: Bool) -> Bool {
        return clickedCampaigns.campaigns.contains(
            where: { $0.id == campaign.id }
        ) == checkValue
    }
    
    func setPlaytimeOptions() async {
        do {
            try await Playtime.setPlaytimeOptions(
                options: PlaytimeOptionsBuilder()
                    .setUserId(Config.userId.rawValue)
                    .setSDKHash(Config.sdkHash.rawValue)
                    .build()
            )
            didSetOptions = true
        } catch {
            print("Error setting options: \(error)")
        }
    }
    
    func fetchCampaigns() async {
        if (loadedCatalog) {
            return
        }
        
        if !didSetOptions {
            await setPlaytimeOptions()
        }
        
        loadingCatalog = true
        
        // Firstly, fetch clickedCampaigns
        fetchClickedCampaigns()
        
        do {
            let _catalogGames = try await Game.getCampaigns()
            let games = _catalogGames.filter { self.filterCatalogCampaigns($0, checkValue: false) }
            catalogGames = IdentifiedArrayOf(uniqueElements: games)
            clickedCatalogGames = IdentifiedArrayOf(
                uniqueElements: _catalogGames
                .filter { self.filterCatalogCampaigns($0, checkValue: true) }
                .map {
                    var updatedGame = $0
                    let clickedGame = self.clickedCampaigns.campaigns[id: updatedGame.id]
                    updatedGame.clickedAt = clickedGame?.clickedAt
                    let clickedAtDate = Date.isoStringToDate(clickedGame?.clickedAt ?? "")
                    updatedGame.clickedDuration = Date.minutesBetweenDates(clickedAtDate ?? Date(), Date())
                    clickedCatalogGames.append(updatedGame)
                    return updatedGame
                }
            )
            startCheckClickedGames()
            loadedCatalog = true
        } catch {
            print("Error fetching campaigns: \(error)")
        }

        loadingCatalog = false
    }

    func fetchInstalledCampaigns() async {
        if (loadedInProgress) {
            return
        }
        
        loadingInProgress = true
        
        do {
            var installedGames: [Game] = []
            installedGames.append(contentsOf: clickedCatalogGames)
            installedGames.append(contentsOf: try await Game.getInstalledCampaigns())
            inProgressGames = IdentifiedArrayOf(uniqueElements: installedGames)
            loadedInProgress = true
        } catch {
            print("Error fetching installed campaigns: \(error)")
        }
        
        loadingInProgress = false
    }
    
    func openChatbot() {
        Task {
            do {
                try await PlaytimeStudio.openChatbot(campaign: nil)
            } catch {
                print("Error opening chatbot: \(error)")
            }
        }
    }
    
    func openChatbotForCampaign(campaign: PlaytimeCampaign) {
        Task {
            do {
                try await PlaytimeStudio.openChatbot(campaign: campaign)
            } catch {
                print("Error opening chatbot: \(error)")
            }
        }
    }

    func deeplinkInstalledApps() {
        Task {
            do {
                try await PlaytimeStudio.showInstalledApps()
            } catch {
                print("Error showing installed apps: \(error)")
            }
        }
    }
}
