import Foundation
import SwiftUI
import Combine

struct GamificationCalculator {
    static func calculateLevel(points: Int) -> Int {
        if points < 100 { return 1 }
        var threshold = 100
        var level = 2
        while points >= threshold {
            threshold += 50 * (level - 1)
            level += 1
        }
        return level - 1
    }

    static func calculatePointsForNextLevel(currentLevel: Int) -> Int {
        return 100 + (currentLevel - 1) * 50
    }

    static func toStats(streak: Int, totalPoints: Int) -> UserGamificationStats {
        let level = calculateLevel(points: totalPoints)
        let pointsForNext = calculatePointsForNextLevel(currentLevel: level)
        let pointsInCurrentLevel = totalPoints - pointsForLevelStart(level: level)
        let pointsToNext = pointsForNext - pointsInCurrentLevel

        return UserGamificationStats(
            level: level,
            currentPoints: streak,
            pointsToNextLevel: max(pointsToNext, 0),
            activeBadges: []
        )
    }

    private static func pointsForLevelStart(level: Int) -> Int {
        if level <= 1 { return 0 }
        var total = 100
        for i in 2..<level {
            total += 100 + (i - 1) * 50
        }
        return total
    }
}

@MainActor
class GamificationManager: ObservableObject {
    static let shared = GamificationManager()

    @AppStorage("streak", store: AppGroupHelper.defaults) var streak: Int = 0
    @AppStorage("total_points", store: AppGroupHelper.defaults) var totalPoints: Int = 0
    @AppStorage("last_opened_timestamp", store: AppGroupHelper.defaults) var lastOpenedTimestamp: Double = 0
    @AppStorage("best_streak", store: AppGroupHelper.defaults) var bestStreak: Int = 0

    var stats: UserGamificationStats {
        GamificationCalculator.toStats(streak: streak, totalPoints: totalPoints)
    }

    func checkDailyOpen() {
        let now = Date().timeIntervalSince1970
        let lastOpen = lastOpenedTimestamp
        let hoursSinceLastOpen = (now - lastOpen) / 3600

        if lastOpen == 0 {
            streak = 1
            totalPoints += 10
            lastOpenedTimestamp = now
            return
        }

        if hoursSinceLastOpen >= 48 {
            streak = 1
            totalPoints += 10
        } else if hoursSinceLastOpen >= 24 {
            streak += 1
            totalPoints += 50
        } else if hoursSinceLastOpen >= 1 {
            totalPoints += 10
        }

        lastOpenedTimestamp = now

        if streak > bestStreak {
            bestStreak = streak
        }
    }
}
