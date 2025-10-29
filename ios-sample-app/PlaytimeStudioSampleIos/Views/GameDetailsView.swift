import SwiftUI
import Foundation
import PlaytimeMonetize

struct GameDetailsView: View {
    let game: Game
    @EnvironmentObject var viewModel: GameViewModel
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                if let landscapeURL = game.landscapeImageURL {
                    AsyncImage(url: landscapeURL) { image in
                        image
                            .resizable()
                            .scaledToFit()
                            .frame(height: 180)
                            .cornerRadius(12)
                            .padding(.horizontal)
                    } placeholder: {
                        ProgressView()
                            .frame(height: 180)
                    }
                }
                
                Text(game.name)
                    .font(.largeTitle)
                    .bold()
                    .padding(.horizontal)
                
                VStack(alignment: .leading, spacing: 10) {
                    Text("Rewards (\(game.levels.count))")
                        .font(.title2)
                        .bold()
                        .padding(.horizontal)
                    
                    ScrollView(.horizontal) {
                        HStack {
                            ForEach(game.levels) {
                                RewardLevel(level: $0)
                            }
                        }
                        .padding()
                    }
                    
                    if game.bonusLevels.count > 0 {
                        Text("Bonus Rewards (\(game.bonusLevels.count))")
                            .font(.title2)
                            .bold()
                            .padding(.horizontal)
                        
                        ScrollView(.horizontal) {
                            HStack {
                                ForEach(game.bonusLevels) {
                                    RewardLevel(level: $0)
                                }
                            }
                            .padding()
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Description")
                            .font(.title2)
                            .bold()
                        
                        Text(game.description)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .padding(.horizontal)
                    
                    Button(action: {
                       Task {
                           await viewModel.onClickPlayGame(game)
                       }
                    }) {
                       Text("Play Now")
                           .font(.subheadline)
                           .foregroundColor(.white)
                           .padding(.vertical, 8)
                           .frame(maxWidth: .infinity)
                           .background(Color.blue)
                           .cornerRadius(8)
                   }
                    
                    .padding(.horizontal)
                    .padding(.top)
                    
                    Spacer(minLength: 40)
                }
            }
            .navigationTitle(game.name)
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
