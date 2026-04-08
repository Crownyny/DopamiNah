package co.edu.unicauca.dopaminah

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main Application class for the app.
 * The @HiltAndroidApp annotation triggers Hilt's code generation, including 
 * a base class for your application that serves as the application-level 
 * dependency container.
 */
@HiltAndroidApp
class DopaminahApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
