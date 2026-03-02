package co.edu.unicauca.dopaminah.di

import android.content.Context
import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GamificationRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGoalsRepository(
        goalsRepositoryImpl: GoalsRepositoryImpl
    ): GoalsRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        gamificationRepositoryImpl: GamificationRepositoryImpl
    ): GamificationRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    
    @Provides
    @Singleton
    fun provideDeviceUsageRepository(
        @ApplicationContext context: Context
    ): DeviceUsageRepository {
        return DeviceUsageRepositoryImpl(context)
    }
}
