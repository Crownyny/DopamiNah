package co.edu.unicauca.dopaminah

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for the app.
 * The @HiltAndroidApp annotation triggers Hilt's code generation, including 
 * a base class for your application that serves as the application-level 
 * dependency container.
 */
@HiltAndroidApp
class DopaminahApplication : Application()
