import Foundation

struct JSONHelper {
    static private let encoder = JSONEncoder()
    static private let jsonDecoder = JSONDecoder()
    
    static func encode(codableObject: Codable) -> String? {
        let data = try! encoder.encode(codableObject)
        return String(data: data, encoding: .utf8)
    }
    
    static func decodeClickedCampaigns(jsonData: String) throws -> ClickedCampaigns {
        return try jsonDecoder.decode(ClickedCampaigns.self, from: jsonData.data(using: .utf8) ?? Data())
    }
}
