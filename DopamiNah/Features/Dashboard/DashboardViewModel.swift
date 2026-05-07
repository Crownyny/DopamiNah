import Foundation
import SwiftData
import Combine

@MainActor
final class DashboardViewModel: ObservableObject {
    @Published var gamificationStats: UserGamificationStats = .init(level: 1, currentPoints: 0, pointsToNextLevel: 100, activeBadges: [])
    @Published var dailyUnlocks: Int = 0
    @Published var yesterdayUnlocks: Int = 0
    @Published var totalDailyUsageMs: Int64 = 0
    @Published var hasUsagePermission: Bool = false
    @Published var dailyUsageStats: [AppUsageSummary] = []
    @Published var appLimitCards: [AppLimitCardInfo] = []
    @Published var isLoading = true

    private let deviceUsageRepo: DeviceUsageRepositoryProtocol
    private let goalsRepo: GoalsRepositoryProtocol

    init(
        deviceUsageRepo: DeviceUsageRepositoryProtocol = MockRepositories.deviceUsage,
        goalsRepo: GoalsRepositoryProtocol = MockRepositories.goals
    ) {
        self.deviceUsageRepo = deviceUsageRepo
        self.goalsRepo = goalsRepo
    }

    func loadData() async {
        isLoading = true

        gamificationStats = GamificationManager.shared.stats

        async let unlocks = deviceUsageRepo.getDailyDeviceUnlocks()
        async let yesterday = deviceUsageRepo.getYesterdayDeviceUnlocks()
        async let usageStats = deviceUsageRepo.getDailyUsageStats()
        async let permission = deviceUsageRepo.hasUsageStatsPermission()
        async let limitCards = deviceUsageRepo.checkUsageLimits()

        dailyUnlocks = await unlocks
        yesterdayUnlocks = await yesterday
        dailyUsageStats = await usageStats
        hasUsagePermission = await permission
        appLimitCards = await limitCards

        totalDailyUsageMs = dailyUsageStats.reduce(0) { $0 + $1.totalTimeForegroundMillis }

        isLoading = false
    }

    func checkAndIncrementStreak() {
        GamificationManager.shared.checkDailyOpen()
        gamificationStats = GamificationManager.shared.stats
    }

    func refreshStats() async {
        dailyUnlocks = await deviceUsageRepo.getDailyDeviceUnlocks()
        yesterdayUnlocks = await deviceUsageRepo.getYesterdayDeviceUnlocks()
    }

    var streakMotivation: String {
        let streak = GamificationManager.shared.streak
        if streak >= 30 {
            return "Increíble! \(streak) días sin parar 🔥"
        } else if streak >= 14 {
            return "Estás en racha! Sigue así 💪"
        } else if streak >= 7 {
            return "Una semana completa! 🎉"
        } else if streak >= 3 {
            return "Buen comienzo! No pares 🚀"
        } else {
            return "Cada día cuenta. Tú puedes ✨"
        }
    }
}
