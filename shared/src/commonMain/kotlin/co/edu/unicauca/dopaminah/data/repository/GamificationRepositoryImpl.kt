package co.edu.unicauca.dopaminah.data.repository

import co.edu.unicauca.dopaminah.DevicePreferences
import co.edu.unicauca.dopaminah.currentTimeMillis
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.utils.GamificationCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Implementation of [GamificationRepository] backed by [DevicePreferences] (platform-native key-value storage). */
class GamificationRepositoryImpl(
    private val prefs: DevicePreferences
) : GamificationRepository {

    private companion object {
        const val KEY_STREAK = "gamification_streak"
        const val KEY_TOTAL_POINTS = "gamification_total_points"
        const val KEY_BEST_STREAK = "gamification_best_streak"
        const val KEY_LAST_OPENED_DAY = "gamification_last_opened_day"
        const val POINTS_PER_DAY = 10
    }

    private val _stats = MutableStateFlow(loadStats())

    override fun getGamificationStats(): Flow<UserGamificationStats> = _stats.asStateFlow()

    override suspend fun checkAndIncrementStreak() {
        val today = currentTimeMillis() / 86_400_000L
        val lastDay = prefs.getLong(KEY_LAST_OPENED_DAY, -1L)

        if (lastDay == today) return

        val streak = if (lastDay == today - 1) {
            prefs.getInt(KEY_STREAK, 0) + 1
        } else {
            1
        }

        val totalPoints = prefs.getInt(KEY_TOTAL_POINTS, 0) + POINTS_PER_DAY
        val bestStreak = maxOf(prefs.getInt(KEY_BEST_STREAK, 0), streak)

        prefs.putInt(KEY_STREAK, streak)
        prefs.putInt(KEY_TOTAL_POINTS, totalPoints)
        prefs.putInt(KEY_BEST_STREAK, bestStreak)
        prefs.putLong(KEY_LAST_OPENED_DAY, today)

        _stats.value = GamificationCalculator.toStats(streak, totalPoints, bestStreak)
    }

    override suspend fun getStreak(): Int = prefs.getInt(KEY_STREAK, 0)

    override suspend fun getBestStreak(): Int = prefs.getInt(KEY_BEST_STREAK, 0)

    override suspend fun getTotalPoints(): Int = prefs.getInt(KEY_TOTAL_POINTS, 0)

    private fun loadStats(): UserGamificationStats {
        val streak = prefs.getInt(KEY_STREAK, 0)
        val totalPoints = prefs.getInt(KEY_TOTAL_POINTS, 0)
        val bestStreak = prefs.getInt(KEY_BEST_STREAK, 0)
        return GamificationCalculator.toStats(streak, totalPoints, bestStreak)
    }
}
