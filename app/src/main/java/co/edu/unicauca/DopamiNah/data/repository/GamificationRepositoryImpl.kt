package co.edu.unicauca.DopamiNah.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.unicauca.DopamiNah.domain.model.UserGamificationStats
import co.edu.unicauca.DopamiNah.domain.repository.GamificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gamification_prefs")

@Singleton
class GamificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GamificationRepository {

    private object PreferencesKeys {
        val STREAK = intPreferencesKey("streak")
        val TOTAL_POINTS = intPreferencesKey("total_points")
        val LAST_OPENED_TIMESTAMP = longPreferencesKey("last_opened_timestamp")
    }

    override fun getGamificationStats(): Flow<UserGamificationStats> = context.dataStore.data.map { preferences ->
        val streak = preferences[PreferencesKeys.STREAK] ?: 0
        val totalPoints = preferences[PreferencesKeys.TOTAL_POINTS] ?: 0
        
        // Simple level logic: Level 1 = 0 pts, Level 2 = 100 pts, Level 3 = 300 pts...
        val level = calculateLevel(totalPoints)
        val pointsToNextLevel = calculatePointsForNextLevel(level)
        
        UserGamificationStats(
            level = level,
            currentPoints = streak, // Using currentPoints to pass down streak to UI as per existing model. (Refactor: UI expects 'currentPoints' as current progress, streak could be handled separately).
            pointsToNextLevel = pointsToNextLevel,
            activeBadges = emptyList()
        )
    }

    override suspend fun incrementStreakAndPoints() {
        context.dataStore.edit { preferences ->
            val lastOpenedMillis = preferences[PreferencesKeys.LAST_OPENED_TIMESTAMP] ?: 0L
            val currentStreak = preferences[PreferencesKeys.STREAK] ?: 0
            
            val today = Calendar.getInstance()
            val lastOpened = Calendar.getInstance()
            lastOpened.timeInMillis = lastOpenedMillis

            // Zero out time fields to compare only days
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            lastOpened.set(Calendar.HOUR_OF_DAY, 0)
            lastOpened.set(Calendar.MINUTE, 0)
            lastOpened.set(Calendar.SECOND, 0)
            lastOpened.set(Calendar.MILLISECOND, 0)

            val diffMillis = today.timeInMillis - lastOpened.timeInMillis
            val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

            when {
                lastOpenedMillis == 0L || diffDays > 1 -> {
                    // Missed a day or first time
                    preferences[PreferencesKeys.STREAK] = 1
                    preferences[PreferencesKeys.TOTAL_POINTS] = (preferences[PreferencesKeys.TOTAL_POINTS] ?: 0) + 10 // 10 pts for returning/starting
                }
                diffDays == 1 -> {
                    // Opened yesterday, increment streak
                    preferences[PreferencesKeys.STREAK] = currentStreak + 1
                    preferences[PreferencesKeys.TOTAL_POINTS] = (preferences[PreferencesKeys.TOTAL_POINTS] ?: 0) + 50 // 50 pts per consecutive day
                }
                diffDays == 0 -> {
                    // Already opened today, do nothing or just update timestamp.
                }
            }
            
            // Save today as last opened
            preferences[PreferencesKeys.LAST_OPENED_TIMESTAMP] = System.currentTimeMillis()
        }
    }
    
    // Simplistic progression: Level n requires (n*100) points.
    private fun calculateLevel(points: Int): Int {
        var lvl = 1
        var threshold = 100
        var currentPoints = points
        
        while(currentPoints >= threshold) {
            lvl++
            currentPoints -= threshold
            threshold += 50 // Next level gets harder
        }
        return lvl
    }
    
    private fun calculatePointsForNextLevel(currentLevel: Int): Int {
         return 100 + ((currentLevel - 1) * 50)
    }
}
