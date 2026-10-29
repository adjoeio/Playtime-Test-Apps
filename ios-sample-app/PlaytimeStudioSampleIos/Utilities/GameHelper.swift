import Foundation
import PlaytimeSDK
import IdentifiedCollections

extension GameLevel {
    func isTimeBoosted() -> Bool {
        guard let timedCoinsDuration = self.reward?.timedCoinsDuration else {
            return false
        }
        
        return timedCoinsDuration > 0
    }
    
    func isRepeatable() -> Bool {
        guard let rewardsCount = self.reward?.rewardsCount else {
            return false
        }
        
        return rewardsCount > 1
    }
    
    func hasCashback() -> Bool {
        if let _ = self.cashback {
            return true
        }
        
        return false
    }
}

extension Game {
    static func convertCampaignToGame(_ campaign: PlaytimeCampaign) -> Game {
        let levels = campaign.eventConfig?.sequentialActions ?? []
        
        let allGameLevels = levels
            .map {
                GameLevel(
                    taskDescription: $0.taskDescription ?? "Default Name",
                    points: $0.amount,
                    rewardedAt: $0.rewardedAt,
                    reward: $0,
                    cashback: nil
                )
            }
        
        let rewards = allGameLevels.filter { !$0.isTimeBoosted() }
        let timeBoostedLevels = allGameLevels.filter { $0.isTimeBoosted() }
        
        var bonusLevels: [GameLevel] = []
        
        // Append Cashback
        if (campaign.eventConfig?.cashbackReward != nil) {
            bonusLevels.append(GameLevel(
                taskDescription: "Make in-app purches",
                points: Int(campaign.eventConfig?.cashbackReward?.maxLimitPerCampaignCoins ?? 0.0),
                rewardedAt: nil,
                reward: nil,
                cashback: campaign.eventConfig?.cashbackReward
            ))
        }
        
        // Time Boosted
        timeBoostedLevels.forEach {
            bonusLevels.append($0)
        }
        
        // Bonus Actions
        campaign.eventConfig?.bonusActions.forEach {
            bonusLevels.append(GameLevel(
                taskDescription: $0.taskDescription ?? "Default Name",
                points: $0.amount,
                rewardedAt: $0.rewardedAt,
                reward: $0,
                cashback: nil
            ))
        }
        
        // Extract icon and landscape image URLs
        let iconURL = URL(string: campaign.iconImage ?? "")
        let landscapeURL = URL(string: campaign.image?.landscape ?? "")
        
        return Game(
            id: campaign.campaignUUID,
            campaign: campaign,
            name: campaign.appName,
            maxPerTask: allGameLevels.map { $0.points }.max() ?? 0,
            totalPoints: allGameLevels.map { $0.points }.reduce(0, +),
            iconName: "gamecontroller",
            iconURL: iconURL,
            landscapeImageURL: landscapeURL,
            levels: rewards,
            bonusLevels: bonusLevels,
            description: campaign.appDescription,
            promoName: campaign.promotion?.name,
            promoDescription: campaign.promotion?.promotionDescription,
            boostFactor: campaign.promotion?.boostFactor,
            startTime: campaign.promotion?.startTime,
            endTime: campaign.promotion?.endTime,
            isExpired: campaign.status == .failed,
            isPending: campaign.status == .pending,
        )
    }
    
    static func getCampaigns() async throws -> IdentifiedArrayOf<Game> {
        let options = getCampaignOptions()
        let response = try await PlaytimeStudio.getCampaigns(campaignOptions: options)
        let games = response.campaigns.map(convertCampaignToGame)
        return IdentifiedArrayOf(uniqueElements: games)
    }
    
    static func getInstalledCampaigns() async throws -> IdentifiedArrayOf<Game> {
        let options = getInstalledCampaignOptions()
        let response = try await PlaytimeStudio.getInstalledCampaigns(options: options)
        let games = response.campaigns.map(convertCampaignToGame)
        return IdentifiedArrayOf(uniqueElements: games)
    }
    
    static func getInitOptions() -> InitOptions {
        return InitOptions(
            sdkHash: Config.sdkHash.rawValue,
            userId: Config.userId.rawValue,
            userProfile: PlaytimeUserProfile(
                gender: PlaytimeGender.male,
                birthday: Date.isoStringToDate("2025-06-26T14:45:30.123Z")
            ),
            extensions: nil,
            language: nil,
            uaNetwork: "uaNetwork",
            uaChannel: "uaChannel",
            uaCampaignName: nil,
            country: nil
        )
    }
    
    static func getCampaignOptions() -> CampaignOptions {
        return CampaignOptions(
            placement: nil,
            category: AppCategory.both,
            displayFormat: nil,
            promotionTag: nil
        )
    }
    
    static func getInstalledCampaignOptions() -> InstalledCampaignOptions {
        return InstalledCampaignOptions(category: AppCategory.both)
    }
}
