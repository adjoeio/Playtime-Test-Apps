import Foundation
import IdentifiedCollections

struct ClickedCampaign: Codable, Identifiable {
    let id: String?
    let clickedAt: String
}

struct ClickedCampaigns: Codable {
    var campaigns: IdentifiedArrayOf<ClickedCampaign>
}
