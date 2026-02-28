package co.edu.unicauca.DopamiNah.di

import android.content.Context
import co.edu.unicauca.DopamiNah.data.repository.DeviceUsageRepositoryImpl
import co.edu.unicauca.DopamiNah.data.repository.GamificationRepositoryImpl
import co.edu.unicauca.DopamiNah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.DopamiNah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.DopamiNah.domain.repository.GamificationRepository
import co.edu.unicauca.DopamiNah.domain.repository.GoalsRepository
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
