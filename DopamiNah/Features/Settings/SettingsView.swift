import SwiftUI
import UIKit

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @EnvironmentObject var themeController: ThemeController

    private var rootViewController: UIViewController? {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let root = windowScene.windows.first?.rootViewController else {
            return nil
        }
        return root
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppSpacing.cardSpacing) {
                    SettingsHeaderView()

                    if let error = viewModel.errorMessage {
                        ErrorCard(message: error, onDismiss: viewModel.clearError)
                    }

                    if !viewModel.isPremium {
                        PremiumCard(
                            onGoogleSignIn: {
                                guard let vc = rootViewController else { return }
                                viewModel.signInWithGoogle(presenting: vc)
                            },
                            onAppleSignIn: {
                                guard let vc = rootViewController else { return }
                                viewModel.signInWithApple(presenting: vc)
                            }
                        )
                    } else {
                        PremiumActiveCard()
                    }

                    SettingsSectionView(title: "Apariencia y Notificaciones") {
                        SettingsToggleItem(
                            icon: themeController.isDarkMode ? "moon.fill" : "sun.max.fill",
                            title: "Modo Oscuro",
                            subtitle: "Reduce la fatiga visual",
                            checked: themeController.isDarkMode,
                            onCheckedChange: { enabled in
                                viewModel.isDarkTheme = enabled
                                themeController.setDarkMode(enabled)
                            },
                            activeColor: .dopaminahPurple
                        )

                        SettingsToggleItem(
                            icon: "bell.fill",
                            title: "Notificaciones",
                            subtitle: "Alertas de límites superados",
                            checked: viewModel.notificationsEnabled,
                            onCheckedChange: viewModel.toggleNotifications,
                            activeColor: .dopaminahOrange
                        )

                        SettingsNavigationItem(
                            icon: "bubble.left.and.bubble.right.fill",
                            title: "Enviar notificación de prueba"
                        ) {
                            viewModel.sendTestNotification()
                        }
                    }

                    if let user = viewModel.currentUser {
                        SettingsSectionView(title: "Cuenta") {
                            UserProfileRow(user: user)

                            SettingsNavigationItem(
                                icon: "arrow.right.square.fill",
                                title: "Cerrar sesión",
                                textColor: .dangerRed
                            ) {
                                viewModel.signOut()
                            }
                        }
                    }

                    SettingsSectionView(title: "Privacidad y Seguridad") {
                        SettingsNavigationItem(icon: "hand.raised.fill", title: "Política de privacidad") {}
                        SettingsNavigationItem(icon: "shield.fill", title: "Permisos de la app") {}
                    }

                    SettingsSectionView(title: "Soporte") {
                        SettingsNavigationItem(icon: "questionmark.circle.fill", title: "Centro de ayuda") {}
                        SettingsNavigationItem(icon: "envelope.fill", title: "Contactar soporte") {}
                        SettingsNavigationItem(icon: "wrench.fill", title: "Datos de Prueba") {
                            viewModel.showDebugData = true
                        }
                    }

                    AboutSection()
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .overlay {
                if viewModel.isSigningIn {
                    Color.black.opacity(0.3)
                        .ignoresSafeArea()
                    ProgressView("Iniciando sesión...")
                        .padding(24)
                        .background(Color.surfaceCard)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
            }
            .sheet(isPresented: $viewModel.showDebugData) {
                DebugDataView()
            }
        }
    }
}

// MARK: - Settings Header
struct SettingsHeaderView: View {
    var body: some View {
        HStack {
            Text("Ajustes")
                .font(AppTypography.largeTitle())
                .foregroundColor(.textPrimary)
            Spacer()
        }
    }
}

// MARK: - Error Card
struct ErrorCard: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        HStack {
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundColor(.dangerRed)
            Text(message)
                .font(AppTypography.body())
                .foregroundColor(.dangerRed)
            Spacer()
            Button(action: onDismiss) {
                Image(systemName: "xmark.circle.fill")
                    .foregroundColor(.dangerRed)
            }
        }
        .padding(12)
        .background(Color.dangerRed.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

// MARK: - Premium Card
struct PremiumCard: View {
    let onGoogleSignIn: () -> Void
    let onAppleSignIn: () -> Void
    @State private var showLoginOptions = false

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 8) {
                        Image(systemName: "crown.fill")
                            .foregroundColor(.dopaminahOrange)
                        Text("Activa Premium")
                            .font(AppTypography.title3())
                            .foregroundColor(.textPrimary)
                    }
                    Text("Desbloquea todas las funciones con un solo pago")
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                }
                Spacer()
            }

            VStack(alignment: .leading, spacing: 8) {
                PremiumPerkItem(text: "Estadísticas avanzadas ilimitadas")
                PremiumPerkItem(text: "Metas sin límite")
                PremiumPerkItem(text: "Exportar reportes")
            }

            HStack {
                Text("$9.99 pago único")
                    .font(AppTypography.headline())
                    .foregroundColor(.dopaminahOrange)
                Spacer()
            }

            VStack(spacing: 8) {
                Button(action: onAppleSignIn) {
                    HStack {
                        Image(systemName: "apple.logo")
                            .font(.system(size: 18))
                        Text("Continuar con Apple")
                            .font(AppTypography.headline())
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.black)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }

                Button(action: onGoogleSignIn) {
                    HStack {
                        Image(systemName: "g.circle.fill")
                            .font(.system(size: 18))
                        Text("Continuar con Google")
                            .font(AppTypography.headline())
                    }
                    .foregroundColor(.dopaminahPurple)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
            }
        }
        .padding(20)
        .background(
            LinearGradient(
                colors: [.dopaminahOrange.opacity(0.15), .dopaminahPurple.opacity(0.08)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .overlay(
            RoundedRectangle(cornerRadius: AppSpacing.cardRadius)
                .stroke(Color.dopaminahOrange, lineWidth: 2)
        )
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct PremiumPerkItem: View {
    let text: String

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "checkmark.circle.fill")
                .foregroundColor(.successGreen)
                .font(.system(size: 14))
            Text(text)
                .font(AppTypography.body())
                .foregroundColor(.textPrimary)
        }
    }
}

// MARK: - Premium Active Card
struct PremiumActiveCard: View {
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "checkmark.seal.fill")
                .font(.system(size: 32))
                .foregroundColor(.successGreen)

            VStack(alignment: .leading, spacing: 4) {
                Text("Eres Premium")
                    .font(AppTypography.headline())
                    .foregroundColor(.successGreen)
                Text("Todas las funciones desbloqueadas")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()
        }
        .padding(16)
        .background(Color.successGreen.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Settings Section
struct SettingsSectionView<Content: View>: View {
    let title: String
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(AppTypography.headline())
                .foregroundColor(.textSecondary)

            VStack(spacing: 0) {
                content()
            }
            .padding(16)
            .background(Color.surfaceCard)
            .cardShadow()
            .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
        }
    }
}

// MARK: - Settings Toggle Item
struct SettingsToggleItem: View {
    let icon: String
    let title: String
    let subtitle: String
    let checked: Bool
    let onCheckedChange: (Bool) -> Void
    let activeColor: Color

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(checked ? activeColor : .textSecondary)
                .frame(width: 28)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Text(subtitle)
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { checked },
                set: { onCheckedChange($0) }
            ))
            .toggleStyle(SwitchToggleStyle(tint: activeColor))
            .labelsHidden()
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Settings Navigation Item
struct SettingsNavigationItem: View {
    let icon: String
    let title: String
    var textColor: Color = .textPrimary
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.system(size: 20))
                    .foregroundColor(textColor)
                    .frame(width: 28)

                Text(title)
                    .font(AppTypography.body())
                    .foregroundColor(textColor)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.textSecondary.opacity(0.5))
            }
            .padding(.vertical, 8)
        }
    }
}

// MARK: - User Profile Row
struct UserProfileRow: View {
    let user: AuthUser

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: 44, height: 44)
                Text(user.initials)
                    .font(AppTypography.headline())
                    .foregroundColor(.dopaminahPurple)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(user.displayName ?? "Usuario")
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Text(user.email ?? "")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()
        }
        .padding(.vertical, 8)
    }
}

// MARK: - About Section
struct AboutSection: View {
    var body: some View {
        VStack(spacing: 12) {
            Image("AppLogo")
                .resizable()
                .frame(width: 64, height: 64)
                .clipShape(RoundedRectangle(cornerRadius: 14))

            Text("DopamiNah")
                .font(AppTypography.title3())
                .foregroundColor(.textPrimary)

            Text("v1.0.0")
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)

            Text("Tu bienestar digital, tu control.")
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)
        }
        .padding(24)
    }
}
