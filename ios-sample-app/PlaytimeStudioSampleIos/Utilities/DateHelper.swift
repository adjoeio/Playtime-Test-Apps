import Foundation

extension Date {
    static private let iso8601Formatter = ISO8601DateFormatter()
    
    static func minutesBetweenDates(_ oldDate: Date, _ newDate: Date) -> Int {
        return Int(newDate.timeIntervalSince(oldDate) / 60)
    }
    
    static func isoStringToDate(_ isoString: String) -> Date? {
        return iso8601Formatter.date(from: isoString)
    }
}
