package co.edu.unicauca.dopaminah.di

import android.content.Context
import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GamificationRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.UsageMonitoringRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.AuthRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.PremiumRepositoryImpl
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import co.edu.unicauca.dopaminah.domain.repository.AuthRepository
import co.edu.unicauca.dopaminah.domain.repository.PremiumRepository
import co.edu.unicauca.dopaminah.ui.theme.ThemeController
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

    @Binds
    @Singleton
    abstract fun bindUsageMonitoringRepository(
        usageMonitoringRepositoryImpl: UsageMonitoringRepositoryImpl
    ): UsageMonitoringRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPremiumRepository(
        premiumRepositoryImpl: PremiumRepositoryImpl
    ): PremiumRepository
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

    @Provides
    @Singleton
    fun provideThemeController(
        @ApplicationContext context: Context
    ): ThemeController {
        return ThemeController(context)
    }
}
