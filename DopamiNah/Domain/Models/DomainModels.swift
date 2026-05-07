import Foundation

struct AppUsageSummary: Identifiable, Equatable {
    let id = UUID()
    let packageName: String
    let appName: String
    let totalTimeForegroundMillis: Int64
    let unlockCount: Int
    let lastTimeUsed: Date

    static let placeholder = AppUsageSummary(
        packageName: "",
        appName: "Sin datos",
        totalTimeForegroundMillis: 0,
        unlockCount: 0,
        lastTimeUsed: Date()
    )
}

struct DailyDetailStats {
    let dateLabel: String
    let firstUseTime: String
    let avgSessionMinutes: Int
    let mostUsedAppName: String
    let mostUsedAppTime: String
    let unlocks: Int
    let totalTimeMillis: Int64

    static let placeholder = DailyDetailStats(
        dateLabel: "-",
        firstUseTime: "-",
        avgSessionMinutes: 0,
        mostUsedAppName: "-",
        mostUsedAppTime: "-",
        unlocks: 0,
        totalTimeMillis: 0
    )
}

struct MonitoringStats {
    let totalScreenTimeMillis: Int64
    let unlockCount: Int
    let lastResetDate: String
}

struct AppUsageEntry: Identifiable {
    let id = UUID()
    let appName: String
    let averageHours: Float
}

struct BadgeUi: Identifiable {
    let id = UUID()
    let emoji: String
    let title: String
    let description: String
    let unlockDate: String?
    let isUnlocked: Bool

    init(emoji: String, title: String, description: String, unlockDate: String? = nil) {
        self.emoji = emoji
        self.title = title
        self.description = description
        self.unlockDate = unlockDate
        self.isUnlocked = unlockDate != nil
    }
}

struct GoalDisplayModel: Identifiable {
    let id: Int
    let goalType: String
    let appPackageName: String?
    let title: String
    let subtitle: String
    let progressLabel: String
    let progressPercent: String
    let progressFraction: Float
    let isExceeded: Bool
    let currentLimitMinutes: Int
}
