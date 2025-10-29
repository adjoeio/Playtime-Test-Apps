import SwiftUI
import Foundation

struct RewardLevel: View {
    let level: GameLevel
    
    var body: some View {
        VStack {
            if level.isTimeBoosted() {
                Text(level.reward?.timedCoinsDuration?.humanizeMinutes() ?? "Time Boosted")
                    .foregroundColor(.secondary)
            }
            
            if level.isRepeatable() || level.hasCashback() {
                Text("Repeatable")
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text(level.taskDescription)
            
            if level.rewardedAt != nil {
                Text("Rewarded at: \((level.rewardedAt ?? "").formatDate())")
            }
            
            Text("\(level.points) pts")
                .foregroundColor(.secondary)
            
            Spacer()
            
            if level.isRepeatable() {
                Text("\(level.reward?.rewardsCount ?? 0) times")
                    .foregroundColor(.secondary)
            }
            
            if level.hasCashback() {
                Text("\(level.cashback?.exchangeRate ?? 0) for $1")
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}
