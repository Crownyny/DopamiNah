import Foundation
import UIKit

@MainActor
final class MockRepositories {
    static let goals = MockGoalsRepository()
    static let deviceUsage = DeviceUsageRepositoryMock()
    static let gamification = MockGamificationRepository()
    static let auth = MockAuthRepository()
    static let premium = MockPremiumRepository()
}

@MainActor
final class MockGoalsRepository: GoalsRepositoryProtocol {
    private var goals: [AppLimitGoal] = [
        AppLimitGoal(goalType: GoalType.appLimit, packageName: "com.instagram.android", appDisplayName: "Instagram", maxTimeMillis: 3_600_000, currentStreak: 4),
        AppLimitGoal(goalType: GoalType.totalDaily, appDisplayName: "Tiempo Total", maxTimeMillis: 7_200_000, currentStreak: 2),
        AppLimitGoal(goalType: GoalType.unlockLimit, appDisplayName: "Desbloqueos", maxUnlocks: 50, currentStreak: 7)
    ]

    func getAllGoals() async -> [AppLimitGoal] { goals }

    func saveGoal(_ goal: AppLimitGoal) async {
        goals.append(goal)
    }

    func deleteGoal(id: UUID) async {
        goals.removeAll { $0.id == id }
    }

    func updateGoal(_ goal: AppLimitGoal) async {
        if let index = goals.firstIndex(where: { $0.id == goal.id }) {
            goals[index] = goal
        }
    }
}

@MainActor
final class MockGamificationRepository: GamificationRepositoryProtocol {
    func getGamificationStats() async -> UserGamificationStats {
        UserGamificationStats(
            level: 5,
            currentPoints: 7,
            pointsToNextLevel: 150,
            activeBadges: ["early_bird", "focus_master"]
        )
    }

    func incrementStreakAndPoints() async {
    }
}

@MainActor
final class MockAuthRepository: AuthRepositoryProtocol {
    var user: AuthUser? = AuthUser(
        uid: "test_user_123",
        email: "test@dopaminah.com",
        displayName: "Usuario Demo",
        photoURL: nil
    )

    var currentUser: AuthUser? {
        get async { user }
    }

    var currentUserPublisher: AsyncStream<AuthUser?> {
        AsyncStream { continuation in
            continuation.yield(user)
        }
    }

    func getCurrentUser() -> AuthUser? { user }

    func signInWithGoogle(presenting: UIViewController) async throws -> AuthUser { user! }
    func signInWithApple(presenting: UIViewController) async throws -> AuthUser { user! }
    func signOut() async { user = nil }
}

@MainActor
final class MockPremiumRepository: PremiumRepositoryProtocol {
    func getPremiumStatus(userId: String) async -> UserPremiumStatus {
        UserPremiumStatus(userId: userId, isPremium: false, activationDate: nil, expiryDate: nil)
    }

    func setPremiumStatus(userId: String, isPremium: Bool) async throws {}

    func isPremiumUser(userId: String) async -> Bool { false }
}
