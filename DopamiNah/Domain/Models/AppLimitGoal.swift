import Foundation
import SwiftData

@Model
final class AppLimitGoal {
    @Attribute(.unique) var id: UUID
    var goalType: String
    var packageName: String
    var appDisplayName: String
    var maxTimeMillis: Int64
    var maxUnlocks: Int
    var currentStreak: Int

    init(
        id: UUID = UUID(),
        goalType: String,
        packageName: String = "",
        appDisplayName: String = "",
        maxTimeMillis: Int64 = 0,
        maxUnlocks: Int = 0,
        currentStreak: Int = 0
    ) {
        self.id = id
        self.goalType = goalType
        self.packageName = packageName
        self.appDisplayName = appDisplayName
        self.maxTimeMillis = maxTimeMillis
        self.maxUnlocks = maxUnlocks
        self.currentStreak = currentStreak
    }
}

struct GoalType {
    static let totalDaily = "TOTAL_DAILY"
    static let appLimit = "APP_LIMIT"
    static let unlockLimit = "UNLOCK_LIMIT"
}

struct AppLimitCardInfo: Identifiable {
    let id = UUID()
    let packageName: String
    let appName: String
    let timeUsedMs: Int64
    let timeLimitMs: Int64
}
