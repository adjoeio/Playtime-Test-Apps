import Foundation
import PlaytimeSDK
import IdentifiedCollections

@MainActor
final class GameViewModel: ObservableObject {
    @Published var catalogGames: IdentifiedArrayOf<Game> = []
    @Published var loadingCatalog: Bool = true
    @Published var loadedCatalog: Bool = false
    @Published var inProgressGames: IdentifiedArrayOf<Game> = []
    @Published var loadingInProgress: Bool = true
    @Published var loadedInProgress: Bool = false
    // TOS state
    @Published var tosAccepted: Bool = false
    @Published var showTosDrawer: Bool = false

    var didSetOptions: Bool = false
    
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
            try await PlaytimeStudio.openInStore(campaign: campaign, useInlineInstall: true)
        } catch {
            print("Error opening store: \(error)")
        }
    }
    
    func initPlaytime() async {
        do {
            try await Playtime.initPlaytime(
                options: Game.getInitOptions()
            )
            didSetOptions = true
        } catch {
            print("Error setting options: \(error)")
        }
    }

    /// Initializes the SDK (if needed) and checks whether the TOS has already
    /// been accepted. Shows the TOS drawer when consent is still required.
    func checkTosStatus() async {
        if !didSetOptions {
            await initPlaytime()
        }

        do {
            let status = try await Playtime.getStatus()
            tosAccepted = status.isTosAccepted
            showTosDrawer = !status.isTosAccepted
        } catch {
            print("Error getting status: \(error)")
            // If we can't determine status, ask for consent to be safe.
            tosAccepted = false
            showTosDrawer = true
        }
    }

    /// Records the user's acceptance of the TOS and, on success, triggers the
    /// initial campaigns load.
    func acceptTos() async {
        do {
            try await PlaytimeStudio.updateTosConsent(action: .accept)
            tosAccepted = true
            showTosDrawer = false
            await fetchCampaigns()
        } catch {
            print("Error accepting TOS: \(error)")
        }
    }

    func fetchCampaigns() async {
        // Do not fetch campaigns until the user has accepted the TOS.
        guard tosAccepted else {
            showTosDrawer = true
            return
        }

        if (loadedCatalog) {
            return
        }
        
        if !didSetOptions {
            await initPlaytime()
        }
        
        loadingCatalog = true
        
        do {
            catalogGames = IdentifiedArrayOf(uniqueElements: try await Game.getCampaigns())
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
            inProgressGames = IdentifiedArrayOf(uniqueElements: try await Game.getInstalledCampaigns())
            loadedInProgress = true
        } catch {
            print("Error fetching installed campaigns: \(error)")
        }
        
        loadingInProgress = false
    }

    func refreshCampaigns() async {
        loadedCatalog = false
        await fetchCampaigns()
    }

    func refreshInstalledCampaigns() async {
        loadedInProgress = false
        await fetchInstalledCampaigns()
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
                try await PlaytimeStudio.showInstalledCampaigns()
            } catch {
                print("Error showing installed apps: \(error)")
            }
        }
    }
}
