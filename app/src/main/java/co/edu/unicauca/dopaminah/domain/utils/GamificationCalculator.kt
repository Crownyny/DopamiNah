package co.edu.unicauca.dopaminah.domain.utils

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats

object GamificationCalculator {

    fun calculateLevel(points: Int): Int {
        var lvl = 1
        var threshold = 100
        var currentPoints = points
        
        while (currentPoints >= threshold) {
            lvl++
            currentPoints -= threshold
            threshold += 50 // Next level gets harder
        }
        return lvl
    }
    
    fun calculatePointsForNextLevel(currentLevel: Int): Int {
        return 100 + ((currentLevel - 1) * 50)
    }
    
    fun toStats(streak: Int, totalPoints: Int): UserGamificationStats {
        val level = calculateLevel(totalPoints)
        val pointsToNextLevel = calculatePointsForNextLevel(level)
        
        return UserGamificationStats(
            level = level,
            currentPoints = streak, // Using currentPoints to pass down streak to UI as per existing model
            pointsToNextLevel = pointsToNextLevel,
            activeBadges = emptyList()
        )
    }
}
