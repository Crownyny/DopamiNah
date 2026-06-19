package co.edu.unicauca.dopaminah

/** Platform-aware greeting used by the initial template. */
class Greeting {
    private val platformName = getPlatformName()

    fun greet(): String {
        return sayHello(platformName)
    }
}
