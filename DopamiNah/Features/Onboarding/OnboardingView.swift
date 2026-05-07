import SwiftUI
import FamilyControls
import Combine

@MainActor
final class OnboardingViewModel: ObservableObject {
    @Published var currentPage = 0
    @Published var isAuthorized = false
    @Published var notificationsEnabled = false
    let totalPages = 3

    var canProceed: Bool {
        switch currentPage {
        case 0: return true
        case 1: return true
        case 2: return isAuthorized
        default: return false
        }
    }

    func requestScreenTimeAuthorization() async {
        do {
            try await AuthorizationCenter.shared.requestAuthorization(for: .individual)
            isAuthorized = true
        } catch {
            print("Screen Time authorization denied: \(error)")
        }
    }

    func requestNotifications() async {
        let granted = await NotificationHelper.shared.requestAuthorization()
        notificationsEnabled = granted
    }

    func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
}

struct OnboardingView: View {
    @StateObject private var viewModel = OnboardingViewModel()
    @AppStorage("onboarding_completed", store: AppGroupHelper.defaults) var onboardingCompleted = false

    var body: some View {
        NavigationStack {
            TabView(selection: $viewModel.currentPage) {
                OnboardingPageView(
                    icon: "iphone",
                    iconTint: .dopaminahPurple,
                    title: "Controla tu tiempo",
                    description: "Necesitamos acceso a tus estadísticas de uso para ayudarte a gestionar tu tiempo en pantalla de forma consciente.",
                    badge: "Seguro y privado",
                    actionText: "Siguiente",
                    action: {
                        withAnimation {
                            viewModel.currentPage = 1
                        }
                    },
                    isAuthorized: $viewModel.isAuthorized
                )
                .tag(0)

                OnboardingPageView(
                    icon: "bell.badge.fill",
                    iconTint: .dopaminahOrange,
                    title: "Mantente informado",
                    description: "Te notificaremos cuando estés cerca de superar tus límites. Activa las notificaciones para no perderte nada.",
                    badge: "Notificaciones útiles",
                    actionText: "Siguiente",
                    action: {
                        Task {
                            await viewModel.requestNotifications()
                            withAnimation {
                                viewModel.currentPage = 2
                            }
                        }
                    },
                    isAuthorized: $viewModel.isAuthorized
                )
                .tag(1)

                OnboardingPageView(
                    icon: "shield.lefthalf.filled",
                    iconTint: .successGreen,
                    title: "Último paso",
                    description: "Activa el permiso de Tiempo en Pantalla para poder monitorear tu uso y establecer límites efectivos.",
                    badge: "Requerido",
                    actionText: viewModel.isAuthorized ? "Comenzar" : "Activar permiso",
                    action: {
                        if viewModel.isAuthorized {
                            onboardingCompleted = true
                        } else {
                            Task {
                                await viewModel.requestScreenTimeAuthorization()
                            }
                        }
                    },
                    isFinalPage: true,
                    isAuthorized: $viewModel.isAuthorized
                )
                .tag(2)
            }
            .tabViewStyle(.page(indexDisplayMode: .always))
            .indexViewStyle(.page(backgroundDisplayMode: .always))
            .navigationBarHidden(true)
            .ignoresSafeArea()
        }
    }
}

struct OnboardingPageView: View {
    let icon: String
    let iconTint: Color
    let title: String
    let description: String
    let badge: String
    let actionText: String
    let action: () -> Void
    var isFinalPage: Bool = false
    @Binding var isAuthorized: Bool

    var body: some View {
        VStack(spacing: 32) {
            Spacer()

            OnboardingHeader()

            VStack(spacing: 24) {
                ZStack {
                    Circle()
                        .fill(iconTint.opacity(0.15))
                        .frame(width: 120, height: 120)

                    Image(systemName: icon)
                        .font(.system(size: 48))
                        .foregroundColor(iconTint)
                }

                Text(title)
                    .font(AppTypography.largeTitle())
                    .foregroundColor(.textPrimary)
                    .multilineTextAlignment(.center)

                Text(description)
                    .font(AppTypography.body())
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, AppSpacing.horizontalPadding)

                HStack(spacing: 8) {
                    Image(systemName: "shield.fill")
                        .font(.caption)
                        .foregroundColor(.successGreen)
                    Text(badge)
                        .font(AppTypography.caption())
                        .foregroundColor(.successGreen)
                }
                .padding(.vertical, 8)
                .padding(.horizontal, 16)
                .background(Color.successGreen.opacity(0.1))
                .clipShape(Capsule())
            }

            Spacer()

            Button(action: action) {
                HStack {
                    Text(actionText)
                        .font(AppTypography.headline())
                        .foregroundColor(.white)

                    if !isFinalPage {
                        Image(systemName: "arrow.right")
                            .font(.system(size: 16, weight: .semibold))
                    } else if isAuthorized {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 18))
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .background(isFinalPage && !isAuthorized ? Color.dopaminahPurple : Color.dopaminahPurple)
                .clipShape(RoundedRectangle(cornerRadius: AppSpacing.buttonRadius))
            }
            .padding(.horizontal, AppSpacing.horizontalPadding)
            .padding(.bottom, 48)
        }
        .background(Color.backgroundLight.ignoresSafeArea())
    }
}

struct OnboardingHeader: View {
    var body: some View {
        HStack {
            Text("Configuración")
                .font(AppTypography.title2())
                .foregroundColor(.textPrimary)
            Spacer()
        }
        .padding(.horizontal, AppSpacing.horizontalPadding)
        .padding(.top, 24)
    }
}
