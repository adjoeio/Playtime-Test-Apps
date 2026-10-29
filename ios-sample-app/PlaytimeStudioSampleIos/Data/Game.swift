import Foundation
import PlaytimeSDK

struct Game: Identifiable {
    let id: String?
    let campaign: PlaytimeCampaign?
    let name: String
    let maxPerTask: Int
    let totalPoints: Int
    let iconName: String
    let iconURL: URL?
    let landscapeImageURL: URL?
    let levels: [GameLevel]
    let bonusLevels: [GameLevel]
    let description: String
    let promoName: String?
    let promoDescription: String?
    let boostFactor: Float?
    let startTime: String?
    let endTime: String?
    let isExpired: Bool
    let isPending: Bool
}

struct GameLevel: Identifiable {
    let id: UUID = UUID()
    let taskDescription: String
    let points: Int
    let rewardedAt: String?
    let reward: PlaytimeRewardAction?
    let cashback: PlaytimeCashbackConfig?
}
