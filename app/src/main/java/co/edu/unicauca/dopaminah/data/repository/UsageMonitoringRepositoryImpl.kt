package co.edu.unicauca.dopaminah.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import co.edu.unicauca.dopaminah.domain.repository.MonitoringStats
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.monitoringDataStore: DataStore<Preferences> by preferencesDataStore(name = "usage_monitoring")

@Singleton
class UsageMonitoringRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageMonitoringRepository {

    private object PreferencesKeys {
        val TOTAL_SCREEN_TIME = longPreferencesKey("total_screen_time")
        val UNLOCK_COUNT = intPreferencesKey("unlock_count")
        val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
        val LAST_SCREEN_ON_TIME = longPreferencesKey("last_screen_on_time")
    }

    override fun getMonitoringStats(): Flow<MonitoringStats> {
        return context.monitoringDataStore.data.map { preferences ->
            MonitoringStats(
                totalScreenTimeMillis = preferences[PreferencesKeys.TOTAL_SCREEN_TIME] ?: 0L,
                unlockCount = preferences[PreferencesKeys.UNLOCK_COUNT] ?: 0,
                lastResetDate = preferences[PreferencesKeys.LAST_RESET_DATE] ?: ""
            )
        }
    }

    override suspend fun updateScreenTime(durationMillis: Long) {
        context.monitoringDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TOTAL_SCREEN_TIME] ?: 0L
            preferences[PreferencesKeys.TOTAL_SCREEN_TIME] = current + durationMillis
        }
    }

    override suspend fun incrementUnlockCount() {
        context.monitoringDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.UNLOCK_COUNT] ?: 0
            preferences[PreferencesKeys.UNLOCK_COUNT] = current + 1
        }
    }

    override suspend fun resetDailyStats(date: String) {
        context.monitoringDataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_SCREEN_TIME] = 0L
            preferences[PreferencesKeys.UNLOCK_COUNT] = 0
            preferences[PreferencesKeys.LAST_RESET_DATE] = date
        }
    }

    override suspend fun setLastScreenOnTime(timestamp: Long) {
        context.monitoringDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SCREEN_ON_TIME] = timestamp
        }
    }

    override suspend fun getLastScreenOnTime(): Long {
        return context.monitoringDataStore.data.first()[PreferencesKeys.LAST_SCREEN_ON_TIME] ?: 0L
    }
}
