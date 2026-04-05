package co.edu.unicauca.dopaminah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.unicauca.dopaminah.data.local.dao.GoalsDao
import co.edu.unicauca.dopaminah.data.local.entity.AppLimitGoalEntity

@Database(entities = [AppLimitGoalEntity::class], version = 2, exportSchema = false)
abstract class DopaminahDatabase : RoomDatabase() {
    abstract val goalsDao: GoalsDao
    
    companion object {
        const val DATABASE_NAME = "dopaminah_db"
    }
}
