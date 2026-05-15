import Foundation
import SwiftUI
import UIKit
import Combine

@MainActor
final class SettingsViewModel: ObservableObject {
    @Published var isDarkTheme: Bool = false
    @Published var notificationsEnabled: Bool = true
    @Published var pajaroVerdeMode: Bool = false
    @Published var isPremium: Bool = false
    @Published var currentUser: AuthUser?
    @Published var isSigningIn: Bool = false
    @Published var errorMessage: String?
    @Published var showSignInDialog: Bool = false
    @Published var showAppleSignIn: Bool = false
    @Published var showDebugData: Bool = false

    private let authRepo: AuthRepositoryProtocol
    private let premiumRepo: PremiumRepositoryProtocol

    init(
        authRepo: AuthRepositoryProtocol? = nil,
        premiumRepo: PremiumRepositoryProtocol = MockRepositories.premium
    ) {
        self.authRepo = authRepo ?? AuthRepositoryImpl()
        self.premiumRepo = premiumRepo
        loadUser()
    }

    func loadUser() {
        Task {
            currentUser = await authRepo.currentUser
            if let user = currentUser {
                isPremium = await premiumRepo.isPremiumUser(userId: user.uid)
            }
        }
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
        Task {
            let granted = await NotificationHelper.shared.requestAuthorization()
            if granted {
                NotificationHelper.shared.showNotification(
                    id: NotificationHelper.appOpenNotifID,
                    title: "DopamiNah",
                    message: "Esta es una notificación de prueba",
                    isTimeSensitive: true
                )
            }
        }
    }

    func setError(_ error: String) {
        errorMessage = error
    }

    func clearError() {
        errorMessage = nil
    }
}
