package co.edu.unicauca.dopaminah.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.edu.unicauca.dopaminah.currentLocalDayNumber
import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.utils.GamificationCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GamificationRepositoryImpl(
    private val db: DopamiNahDb
) : GamificationRepository {

    private companion object {
        const val POINTS_PER_DAY = 10
    }

    override fun getGamificationStats(): Flow<UserGamificationStats> {
        return db.dopamiNahDbQueries.getGamification()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { row ->
                if (row != null) {
                    GamificationCalculator.toStats(
                        streak = row.streak.toInt(),
                        totalPoints = row.total_points.toInt(),
                        bestStreak = row.best_streak.toInt()
                    )
                } else {
                    UserGamificationStats()
                }
            }
    }

    override suspend fun checkAndIncrementStreak() = withContext(Dispatchers.Default) {
        val today = currentLocalDayNumber()
        val row = db.dopamiNahDbQueries.getGamification().executeAsOneOrNull()
        val lastDay = row?.last_opened_day ?: -1L

        if (lastDay == today) return@withContext

        val streak = if (lastDay == today - 1) {
            (row?.streak?.toInt() ?: 0) + 1
        } else {
            1
        }

        val totalPoints = (row?.total_points?.toInt() ?: 0) + POINTS_PER_DAY
        val bestStreak = maxOf(row?.best_streak?.toInt() ?: 0, streak)

        db.dopamiNahDbQueries.insertOrReplaceGamification(
            streak = streak.toLong(),
            total_points = totalPoints.toLong(),
            best_streak = bestStreak.toLong(),
            last_opened_day = today
        )
    }

    override suspend fun getStreak(): Int = withContext(Dispatchers.Default) {
        db.dopamiNahDbQueries.getGamification()
            .executeAsOneOrNull()?.streak?.toInt() ?: 0
    }

    override suspend fun getBestStreak(): Int = withContext(Dispatchers.Default) {
        db.dopamiNahDbQueries.getGamification()
            .executeAsOneOrNull()?.best_streak?.toInt() ?: 0
    }

    override suspend fun getTotalPoints(): Int = withContext(Dispatchers.Default) {
        db.dopamiNahDbQueries.getGamification()
            .executeAsOneOrNull()?.total_points?.toInt() ?: 0
    }
}
