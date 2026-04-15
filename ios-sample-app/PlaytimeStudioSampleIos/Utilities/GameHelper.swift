import Foundation
import PlaytimeMonetize
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
    enum Constants {
        static let postbackTimeout = 20 // timeout of postback in minutes
        static let timerInterval = 60.0
    }
    
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
            imageName:"gamecontroller2",
            landscapeImageURL: landscapeURL,
            levels: rewards,
            bonusLevels: bonusLevels,
            description: campaign.appDescription,
            featuredPosition: campaign.featuredPosition,
            promoName: campaign.promotion?.name,
            promoDescription: campaign.promotion?.promotionDescription,
            boostFactor: campaign.promotion?.boostFactor,
            startTime: campaign.promotion?.startTime,
            endTime: campaign.promotion?.endTime,
            isCompleted: campaign.isCompleted,
        )
    }
    
    static func getCampaigns() async throws -> IdentifiedArrayOf<Game> {
        let options = try getOptions()
        let response = try await PlaytimeStudio.getCampaigns(options: options)
        let games = response.campaigns.map(convertCampaignToGame)
        return IdentifiedArrayOf(uniqueElements: games)
    }
    
    static func getInstalledCampaigns() async throws -> IdentifiedArrayOf<Game> {
        let options = try getOptions()
        let response = try await PlaytimeStudio.getInstalledCampaigns(options: options)
        let games = response.campaigns.map(convertCampaignToGame)
        return IdentifiedArrayOf(uniqueElements: games)
    }
    
    static func getOptions() throws -> PlaytimeOptions {
        let options = PlaytimeOptionsBuilder()
            .setUserId(Config.userId.rawValue)
            .setUserProfile(
                PlaytimeUserProfileBuilder()
                    .setGender(PlaytimeGender.male)
                    .setBirthday(Date.isoStringToDate("2025-06-26T14:45:30.123Z"))
                    .build()
             )
            .setParams(
                PlaytimeParamsBuilder()
                    .setUAChannel("uaChannel")
                    .setUANetwork("uaNetwork")
                    .setUASubPublisherCleartext("uaSubPublisherCleartext")
                    .setUASubPublisherEncrypted("uaSubPublisherEncrypted")
                    .setPlacement("placement")
                    .build()
            )
            .build()
        return options
    }
}
