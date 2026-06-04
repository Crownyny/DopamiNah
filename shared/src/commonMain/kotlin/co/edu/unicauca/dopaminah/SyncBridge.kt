package co.edu.unicauca.dopaminah

object SyncBridge {
    var onIncomingSync: ((domain: String, timeLimitMinutes: Int) -> Unit)? = null
    var onDomainTimeSync: ((domain: String, spentMinutes: Int) -> Unit)? = null
}
