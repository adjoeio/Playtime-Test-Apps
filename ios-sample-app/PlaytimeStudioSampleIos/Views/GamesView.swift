import SwiftUI
import Foundation
import IdentifiedCollections

enum GameTab: String, CaseIterable {
    case catalog = "Catalog"
    case inProgress = "In Progress"
}

struct GamesView: View {
    @EnvironmentObject var viewModel: GameViewModel
    @State private var selectedTab: GameTab = .catalog

    var body: some View {
        NavigationView {
            VStack {
                Picker("Game Tabs", selection: $selectedTab) {
                    ForEach(GameTab.allCases, id: \.self) { tab in
                        Text(tab.rawValue).tag(tab)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()
                
                Spacer(minLength: 20)
                
                if let promoGame = viewModel.catalogGames.first(where: { $0.boostFactor != nil }) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Name: \(promoGame.promoName ?? "N/A")")
                        Text("Description: \(promoGame.promoDescription ?? "N/A")")
                        Text("Start: \(promoGame.startTime ?? "N/A")")
                        Text("Start: \(promoGame.endTime ?? "N/A")")
                        Text("Boost: \(promoGame.boostFactor ?? 0)")
                        
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(20)
                    .padding(.horizontal)
                }
                
                HStack {
                    Button("Deeplink installed apps") {
                        viewModel.deeplinkInstalledApps()
                    }
                    
                    Button(action: {
                        viewModel.openChatbot()
                     }) {
                        Text("Open Chatbot")
                            .font(.subheadline)
                            .foregroundColor(.white)
                            .padding(8)
                            .background(Color.red)
                            .cornerRadius(8)
                    }
                }
                
                if currentLoading {
                    ProgressView().controlSize(.large)
                }

                ScrollView {
                    VStack(spacing: 16) {
                        ForEach(currentGames) { game in
                            NavigationLink(destination: GameDetailsView(game: game)) {
                                GameRow(game: game)
                            }
                        }
                    }
                    .padding(.top)
                }
                .task {
                    await startup()
                }
                .refreshable {
                    await refreshGames()
                }
                .onChange(of: selectedTab) {
                    Task {
                        await loadGames()
                    }
                }

            }
            .navigationTitle("Games")
            .sheet(isPresented: $viewModel.showTosDrawer) {
                TosDrawerView {
                    await viewModel.acceptTos()
                }
            }
        }
    }
    
    private var currentLoading: Bool {
        switch selectedTab {
            case .catalog:
                return viewModel.loadingCatalog
            case .inProgress:
                return viewModel.loadingInProgress
        }
    }

    private var currentGames: IdentifiedArrayOf<Game> {
        switch selectedTab {
            case .catalog:
                return viewModel.catalogGames
            case .inProgress:
                return viewModel.inProgressGames
        }
    }

    private func startup() async {
        // Ensure the SDK is initialized and TOS status is known before loading.
        if !viewModel.tosAccepted {
            await viewModel.checkTosStatus()
        }
        // Only load games once TOS has been accepted; otherwise the drawer
        // is shown and loading happens after the user accepts.
        if viewModel.tosAccepted {
            await loadGames()
        }
    }

    private func loadGames() async {
        switch selectedTab {
            case .catalog where viewModel.catalogGames.isEmpty:
                await viewModel.fetchCampaigns()
            case .inProgress where viewModel.inProgressGames.isEmpty:
                await viewModel.fetchInstalledCampaigns()
            default:
                break
        }
    }

    private func refreshGames() async {
        switch selectedTab {
            case .catalog:
                await viewModel.refreshCampaigns()
            case .inProgress:
                await viewModel.refreshInstalledCampaigns()
        }
    }
}
