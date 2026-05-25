package co.edu.unicauca.dopaminah

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform