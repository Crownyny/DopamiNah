import Foundation
import SwiftUI
import UIKit
import Combine

@MainActor
final class SettingsViewModel: ObservableObject {
    @Published var isDarkTheme: Bool? = nil
    @Published var notificationsEnabled: Bool = true
    @Published var pajaroVerdeMode: Bool = false
    @Published var isPremium: Bool = false
    @Published var currentUser: AuthUser?
    @Published var isSigningIn: Bool = false
    @Published var errorMessage: String?
    @Published var showSignInDialog: Bool = false
    @Published var showAppleSignIn: Bool = false

    private let authRepo: AuthRepositoryProtocol
    private let premiumRepo: PremiumRepositoryProtocol
    private let themeController: ThemeController

    init(
        authRepo: AuthRepositoryProtocol = MockRepositories.auth,
        premiumRepo: PremiumRepositoryProtocol = MockRepositories.premium,
        themeController: ThemeController
    ) {
        self.authRepo = authRepo
        self.premiumRepo = premiumRepo
        self.themeController = themeController
        isDarkTheme = themeController.isDarkMode
        loadUser()
    }

    convenience init(
        authRepo: AuthRepositoryProtocol = MockRepositories.auth,
        premiumRepo: PremiumRepositoryProtocol = MockRepositories.premium
    ) {
        self.init(authRepo: authRepo, premiumRepo: premiumRepo, themeController: ThemeController())
    }

    func loadUser() {
        Task {
            currentUser = await authRepo.currentUser
            if let user = currentUser {
                isPremium = await premiumRepo.isPremiumUser(userId: user.uid)
            }
        }
    }

    func toggleDarkMode(_ enabled: Bool) {
        isDarkTheme = enabled
        themeController.setDarkMode(enabled)
    }

    func toggleNotifications(_ enabled: Bool) {
        notificationsEnabled = enabled
        if enabled {
            Task {
                _ = await NotificationHelper.shared.requestAuthorization()
            }
        }
    }

    func togglePajaroVerdeMode(_ enabled: Bool) {
        pajaroVerdeMode = enabled
    }

    func signInWithGoogle(presenting: UIViewController) {
        isSigningIn = true
        errorMessage = nil

        Task {
            do {
                let user = try await authRepo.signInWithGoogle(presenting: presenting)
                currentUser = user
                await activatePremium(userId: user.uid)
                isSigningIn = false
                showSignInDialog = false
            } catch {
                errorMessage = error.localizedDescription
                isSigningIn = false
            }
        }
    }

    func signInWithApple(presenting: UIViewController) {
        isSigningIn = true
        errorMessage = nil

        Task {
            do {
                let user = try await authRepo.signInWithApple(presenting: presenting)
                currentUser = user
                await activatePremium(userId: user.uid)
                isSigningIn = false
                showAppleSignIn = false
            } catch {
                errorMessage = error.localizedDescription
                isSigningIn = false
            }
        }
    }

    func signOut() {
        Task {
            await authRepo.signOut()
            currentUser = nil
            isPremium = false
        }
    }

    func activatePremium(userId: String) async {
        do {
            try await premiumRepo.setPremiumStatus(userId: userId, isPremium: true)
            isPremium = true
        } catch {
            errorMessage = "Error al activar premium: \(error.localizedDescription)"
        }
    }

    func sendTestNotification() {
        NotificationHelper.shared.showNotification(
            id: NotificationHelper.appOpenNotifID,
            title: "DopamiNah",
            message: "Esta es una notificación de prueba ✅",
            isTimeSensitive: true
        )
    }

    func setError(_ error: String) {
        errorMessage = error
    }

    func clearError() {
        errorMessage = nil
    }
}
