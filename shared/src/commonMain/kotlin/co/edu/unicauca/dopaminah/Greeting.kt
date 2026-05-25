package co.edu.unicauca.dopaminah

class Greeting {
    private val platformName = getPlatformName()

    fun greet(): String {
        return sayHello(platformName)
    }
}
