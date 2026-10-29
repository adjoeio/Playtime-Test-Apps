import SwiftUI
import Foundation
import PlaytimeSDK

struct GameRow: View {
    let game: Game
    
    var body: some View {
        HStack(spacing: 16) {
            if let url = game.iconURL {
                AsyncImage(url: url) { image in
                    image.resizable()
                } placeholder: {
                    ProgressView()
                }
                .aspectRatio(contentMode: .fit)
                .frame(width: 50, height: 50)
                .cornerRadius(8)
            } else {
                Image(systemName: game.iconName)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50)
                    .foregroundColor(.blue)
            }

            VStack(alignment: .leading) {
                Text(game.name)
                    .font(.headline)
                Text("Max per Task: \(game.maxPerTask)")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                Text("Total Points: \(game.totalPoints)")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                
                if game.isExpired {
                    Text("Installation failed!\nYou won't get rewarded for this campaign.")
                        .font(.subheadline)
                        .foregroundColor(.red)
                }

                if game.isPending {
                    Text("Verifying installation...")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
                
                Button(action: {
                    guard let campaign = game.campaign else {
                        return
                    }
                    
                    Task {
                        try await PlaytimeStudio.showCampaignDetails(campaign: campaign)
                    }
                }) {
                   Text("Deeplink to web catalog")
               }
            }

            Spacer()
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
        .task {
            do {
                guard let campaign = game.campaign else {
                    return
                }
                
                try await PlaytimeStudio.executeCampaignImpression(campaign: campaign)
            } catch {
                print("Error executing engagement: \(error)")
            }
        }
    }
}

#Preview {
    GameRow(
        game: Game(
            id: nil,
            campaign: nil,
            name: "Campaign",
            maxPerTask: 0,
            totalPoints: 0,
            iconName: "",
            iconURL: nil,
            landscapeImageURL: nil,
            levels: [],
            bonusLevels: [],
            description: "",
            promoName: "",
            promoDescription: "",
            boostFactor: 0,
            startTime: "",
            endTime: "",
            isExpired: false,
            isPending: false
        )
    )
}
