package co.edu.unicauca.DopamiNah.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.unicauca.DopamiNah.data.local.dao.GoalsDao
import co.edu.unicauca.DopamiNah.domain.model.AppLimitGoal

@Database(entities = [AppLimitGoal::class], version = 1, exportSchema = false)
abstract class DopaminahDatabase : RoomDatabase() {
    abstract val goalsDao: GoalsDao
    
    companion object {
        const val DATABASE_NAME = "dopaminah_db"
    }
}
