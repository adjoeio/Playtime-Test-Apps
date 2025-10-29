import Foundation

extension String {
    static private let dateFormatter = DateFormatter()
    
    func formatDate() -> String {
        String.dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        let newDate = String.dateFormatter.date(from: self)
        String.dateFormatter.setLocalizedDateFormatFromTemplate("MMMM d, yyyy")
        return String.dateFormatter.string(from: newDate!)
    }
}

extension Int {
    func humanizeMinutes() -> String {
        if self > 60 {
            let hours = self / 60
            
            if self > (60 * 24) {
                let days = hours / 24
                let leftHours = hours % 24
                return "\(days)d \(leftHours)h"
            }
            
            let leftMinutes = self % 60
            return "\(hours)h \(leftMinutes)m"
        }
        
        return "\(self)m 0s"
    }
}
