package co.edu.unicauca.dopaminah

/**
 * Singleton bridge for browser extension ↔ Web App communication.
 *
 * The web app's JS interlayer (extension-bridge.js) receives messages from the
 * Chrome/Edge extension via window.postMessage and dispatches data through
 * these callbacks. `DopamiNahApp.kt` wires them to [WebGoalsViewModel].
 *
 * - [onIncomingSync]: Receives goals created in the extension (domain + time limit)
 * - [onDomainTimeSync]: Receives per-domain accumulated browsing minutes from the extension
 */
object SyncBridge {
    var onIncomingSync: ((domain: String, timeLimitMinutes: Int) -> Unit)? = null
    var onDomainTimeSync: ((domain: String, spentMinutes: Int) -> Unit)? = null
}
