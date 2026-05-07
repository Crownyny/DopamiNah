import Foundation
import UIKit

protocol GoalsRepositoryProtocol {
    func getAllGoals() async -> [AppLimitGoal]
    func saveGoal(_ goal: AppLimitGoal) async
    func deleteGoal(id: UUID) async
    func updateGoal(_ goal: AppLimitGoal) async
}

protocol DeviceUsageRepositoryProtocol {
    func getDailyUsageStats() async -> [AppUsageSummary]
    func getDailyDeviceUnlocks() async -> Int
    func getYesterdayDeviceUnlocks() async -> Int
    func hasUsageStatsPermission() async -> Bool
    func getAverageUsageMillis(days: Int) async -> Int64
    func getAverageUnlocks(days: Int) async -> Int
    func getDailyUsageForLastDays(days: Int) async -> [Int64]
    func getAverageUsagePerApp(days: Int, limit: Int) async -> [(String, Int64)]
    func getDailyDetails(dayOffset: Int) async -> DailyDetailStats
    func getHourlyUsage(days: Int) async -> [Float]
    func checkUsageLimits() async -> [AppLimitCardInfo]
}

protocol GamificationRepositoryProtocol {
    func getGamificationStats() async -> UserGamificationStats
    func incrementStreakAndPoints() async
}

protocol AuthRepositoryProtocol {
    var currentUser: AuthUser? { get async }
    var currentUserPublisher: AsyncStream<AuthUser?> { get }
    func signInWithGoogle(presenting: UIViewController) async throws -> AuthUser
    func signInWithApple(presenting: UIViewController) async throws -> AuthUser
    func signOut() async
    func getCurrentUser() -> AuthUser?
}

protocol PremiumRepositoryProtocol {
    func getPremiumStatus(userId: String) async -> UserPremiumStatus
    func setPremiumStatus(userId: String, isPremium: Bool) async throws
    func isPremiumUser(userId: String) async -> Bool
}
