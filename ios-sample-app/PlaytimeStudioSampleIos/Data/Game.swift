import Foundation
import PlaytimeMonetize

struct Game: Identifiable {
    let id: String?
    let campaign: PlaytimeCampaign
    let name: String
    let maxPerTask: Int
    let totalPoints: Int
    let iconName: String
    let iconURL: URL?
    let imageName: String
    let landscapeImageURL: URL?
    let levels: [GameLevel]
    let bonusLevels: [GameLevel]
    let description: String
    let featuredPosition: Int?
    let promoName: String?
    let promoDescription: String?
    let boostFactor: Float?
    let startTime: String?
    let endTime: String?
    let isCompleted: Bool
    var clickedAt: String? // used to check when game was clicked locally
    var clickedDuration: Int? // used to check how minutes went after clicking
}

struct GameLevel: Identifiable {
    let id: UUID = UUID()
    let taskDescription: String
    let points: Int
    let rewardedAt: String?
    let reward: PlaytimeRewardAction?
    let cashback: PlaytimeCashbackConfig?
}
