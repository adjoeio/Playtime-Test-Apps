import SwiftUI
import Foundation

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
                
                if let _ = game.clickedAt {
                    if game.clickedDuration! >= Game.Constants.postbackTimeout {
                        Text("Postback won't come!")
                            .font(.subheadline)
                            .foregroundColor(.red)
                    } else {
                        Text("Waiting for postback...")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                }
            }

            Spacer()
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}
