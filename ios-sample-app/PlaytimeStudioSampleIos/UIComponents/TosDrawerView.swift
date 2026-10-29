import SwiftUI

/// A bottom drawer presented before any campaigns are loaded. The user must
/// accept the Terms of Service before `getCampaigns` is called.
struct TosDrawerView: View {
    /// Invoked when the user taps "Accept". The parent is responsible for
    /// recording consent and loading campaigns.
    let onAccept: () async -> Void

    @State private var isAccepting: Bool = false

    var body: some View {
        VStack(spacing: 24) {
            Capsule()
                .fill(Color.secondary.opacity(0.4))
                .frame(width: 40, height: 5)
                .padding(.top, 8)

            Image(systemName: "doc.text")
                .font(.system(size: 44))
                .foregroundColor(.blue)

            Text("Terms of Service")
                .font(.title2)
                .fontWeight(.bold)

            ScrollView {
                Text(
                    """
                    Before you can browse and play games, please review and \
                    accept our Terms of Service.

                    By accepting, you agree to participate in the rewards \
                    program, allow us to track your gameplay progress for \
                    reward attribution, and process the data described in our \
                    privacy policy.

                    You can withdraw your consent at any time from the \
                    settings screen.
                    """
                )
                .font(.body)
                .foregroundColor(.secondary)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .frame(maxHeight: 220)

            Button(action: accept) {
                HStack {
                    if isAccepting {
                        ProgressView()
                            .tint(.white)
                    }
                    Text(isAccepting ? "Accepting..." : "Accept & Continue")
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(12)
            }
            .disabled(isAccepting)
        }
        .padding(24)
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.hidden)
        .interactiveDismissDisabled(true)
    }

    private func accept() {
        guard !isAccepting else { return }
        isAccepting = true
        Task {
            await onAccept()
            isAccepting = false
        }
    }
}

#Preview {
    Color.gray
        .sheet(isPresented: .constant(true)) {
            TosDrawerView(onAccept: {})
        }
}
