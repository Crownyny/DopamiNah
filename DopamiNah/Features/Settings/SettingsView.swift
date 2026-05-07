import SwiftUI
import UIKit

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @EnvironmentObject var themeController: ThemeController

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
                                viewModel.showSignInDialog = true
                            },
                            onAppleSignIn: {
                                viewModel.showAppleSignIn = true
                            }
                        )
                    } else {
                        PremiumActiveCard()
                    }

                    SettingsSectionView(title: "Apariencia y Notificaciones") {
                        SettingsToggleItem(
                            icon: viewModel.isDarkTheme == true ? "moon.fill" : "sun.max.fill",
                            title: "Modo Oscuro",
                            subtitle: "Reduce la fatiga visual",
                            checked: viewModel.isDarkTheme ?? false,
                            onCheckedChange: viewModel.toggleDarkMode,
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
                    }

                    AboutSection()
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .sheet(isPresented: $viewModel.showSignInDialog) {
                GoogleSignInDialog(
                    isLoading: viewModel.isSigningIn,
                    errorMessage: viewModel.errorMessage,
                    onSignInSuccess: {},
                    onSignInError: viewModel.setError
                )
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
            ZStack {
                Circle()
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: 40, height: 40)
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 18))
                    .foregroundColor(.dopaminahPurple)
            }
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
                        Text("Eres Premium")
                            .font(AppTypography.title3())
                            .foregroundColor(.textPrimary)
                    }
                    Text("Desbloquea todas las funciones")
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
                    .foregroundColor(.white)
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

// MARK: - Google Sign-In Dialog
struct GoogleSignInDialog: View {
    let isLoading: Bool
    let errorMessage: String?
    let onSignInSuccess: () -> Void
    let onSignInError: (String) -> Void
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Image(systemName: "person.circle")
                    .font(.system(size: 64))
                    .foregroundColor(.dopaminahPurple)

                Text("Iniciar Sesión")
                    .font(AppTypography.title2())
                    .foregroundColor(.textPrimary)

                Text("Inicia sesión para activar Premium y sincronizar tu progreso.")
                    .font(AppTypography.body())
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)

                if let error = errorMessage {
                    Text(error)
                        .font(AppTypography.caption())
                        .foregroundColor(.dangerRed)
                }

                if isLoading {
                    ProgressView()
                }
            }
            .padding(24)
            .presentationDetents([.medium])
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Cancelar") { dismiss() }
                }
            }
        }
    }
}

// MARK: - About Section
struct AboutSection: View {
    var body: some View {
        VStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: 56, height: 56)
                Text("🧠")
                    .font(.system(size: 28))
            }

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
