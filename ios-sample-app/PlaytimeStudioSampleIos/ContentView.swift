import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = GameViewModel()

    var body: some View {
        VStack {
            GamesView()
        }
        .environmentObject(viewModel)
    }
}

#Preview {
    ContentView()
}
