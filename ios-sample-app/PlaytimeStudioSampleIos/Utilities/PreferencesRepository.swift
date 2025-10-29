import Foundation

struct PreferencesRepository {
    private enum Constants {
        static let clickedCampaignsKey = "clickedCampaigns"
        static let defaultClickedCamgaignsValue = "{\"campaigns\": []}"
    }
    
    static func setClickedCampaigns(value: String) {
        UserDefaults.standard.set(value, forKey: Constants.clickedCampaignsKey)
    }
    
    static func getClickedCampaigns() -> String {
        guard let clickedCampaigns = UserDefaults.standard.string(forKey: Constants.clickedCampaignsKey)
        else { return Constants.defaultClickedCamgaignsValue }
        return clickedCampaigns
    }
    
    static func clearClickedCampaigns() {
        self.setClickedCampaigns(value: Constants.clickedCampaignsKey)
    }
}
