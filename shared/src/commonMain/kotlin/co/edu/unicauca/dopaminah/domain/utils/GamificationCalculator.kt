package co.edu.unicauca.dopaminah.domain.utils

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats

object GamificationCalculator {

    fun calculateLevel(points: Int): Int {
        var lvl = 1
        var threshold = 100
        var remaining = points
        while (remaining >= threshold) {
            lvl++
            remaining -= threshold
            threshold += 50
        }
        return lvl
    }

    fun calculatePointsForNextLevel(currentLevel: Int): Int {
        return 100 + ((currentLevel - 1) * 50)
    }

    fun toStats(streak: Int, totalPoints: Int, bestStreak: Int): UserGamificationStats {
        val level = calculateLevel(totalPoints)
        val pointsToNextLevel = calculatePointsForNextLevel(level)
        val currentLevelPoints = totalPoints - calculateTotalPointsForLevel(level - 1)
        return UserGamificationStats(
            level = level,
            currentPoints = currentLevelPoints,
            pointsToNextLevel = pointsToNextLevel,
            streak = streak,
            bestStreak = bestStreak,
            totalPoints = totalPoints
        )
    }

    private fun calculateTotalPointsForLevel(level: Int): Int {
        var total = 0
        var threshold = 100
        for (i in 1..level) {
            total += threshold
            threshold += 50
        }
        return total
    }
}
