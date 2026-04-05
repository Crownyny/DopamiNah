package co.edu.unicauca.dopaminah.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.utils.GamificationCalculator
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
        
        GamificationCalculator.toStats(streak, totalPoints)
    }

    override suspend fun incrementStreakAndPoints() {
        context.dataStore.edit { preferences ->
            val lastOpenedMillis = preferences[PreferencesKeys.LAST_OPENED_TIMESTAMP] ?: 0L
            val currentStreak = preferences[PreferencesKeys.STREAK] ?: 0
            
            val today = Calendar.getInstance()
            val lastOpened = Calendar.getInstance()
            lastOpened.timeInMillis = lastOpenedMillis

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
                    preferences[PreferencesKeys.STREAK] = 1
                    preferences[PreferencesKeys.TOTAL_POINTS] = (preferences[PreferencesKeys.TOTAL_POINTS] ?: 0) + 10
                }
                diffDays == 1 -> {
                    preferences[PreferencesKeys.STREAK] = currentStreak + 1
                    preferences[PreferencesKeys.TOTAL_POINTS] = (preferences[PreferencesKeys.TOTAL_POINTS] ?: 0) + 50
                }
            }
            
            preferences[PreferencesKeys.LAST_OPENED_TIMESTAMP] = System.currentTimeMillis()
        }
    }
}
