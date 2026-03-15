package co.edu.unicauca.dopaminah.di

import android.content.Context
import androidx.room.Room
import co.edu.unicauca.dopaminah.data.local.DopaminahDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDopaminahDatabase(@ApplicationContext context: Context): DopaminahDatabase {
        return Room.databaseBuilder(
            context,
            DopaminahDatabase::class.java,
            DopaminahDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoalsDao(database: DopaminahDatabase) = database.goalsDao
}
