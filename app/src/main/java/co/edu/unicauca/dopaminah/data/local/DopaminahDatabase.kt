package co.edu.unicauca.dopaminah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.unicauca.dopaminah.data.local.dao.GoalsDao
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal

@Database(entities = [AppLimitGoal::class], version = 1, exportSchema = false)
abstract class DopaminahDatabase : RoomDatabase() {
    abstract val goalsDao: GoalsDao
    
    companion object {
        const val DATABASE_NAME = "dopaminah_db"
    }
}
