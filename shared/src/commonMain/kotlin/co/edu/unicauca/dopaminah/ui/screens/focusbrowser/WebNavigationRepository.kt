package co.edu.unicauca.dopaminah.ui.screens.focusbrowser

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import co.edu.unicauca.dopaminah.currentTimeMillis

data class VisitEntry(
    val url: String,
    val host: String,
    val timestamp: Long
)

data class DomainStats(
    val host: String,
    val visitCount: Int,
    val lastVisit: Long
)

class WebNavigationRepository {
    private val _rules = mutableStateListOf<BlockRule>()
    val rules: List<BlockRule> get() = _rules

    private val _visitLog = mutableStateListOf<VisitEntry>()
    val visitLog: List<VisitEntry> get() = _visitLog

    private val _domainStats = mutableStateListOf<DomainStats>()
    val domainStats: List<DomainStats> get() = _domainStats

    var isFocusMode by mutableStateOf(false)
    var sessionStartTime by mutableStateOf(0L)
    var sessionPages by mutableStateOf(0)
    var blockedAttempts by mutableStateOf(0)
    var totalVisits by mutableStateOf(0)

    private var _counter = 0L

    init {
        resetDefaults()
    }

    fun resetDefaults() {
        _rules.clear()
        _rules.addAll(DEFAULT_BLOCKED_DOMAINS.map {
            BlockRule(
                id = (++_counter).toString(),
                domain = it.first,
                label = it.second,
                isWildcard = it.first.startsWith("*.")
            )
        })
    }

    fun startSession() {
        if (sessionStartTime == 0L) {
            sessionStartTime = currentTimeMillis()
            sessionPages = 0
        }
    }

    fun endSession() {
        sessionStartTime = 0L
    }

    fun addRule(domain: String, label: String, isWildcard: Boolean = false) {
        val existing = _rules.find { it.domain == domain }
        if (existing == null) {
            _rules.add(
                BlockRule(
                    id = (++_counter).toString(),
                    domain = domain,
                    label = label,
                    isWildcard = isWildcard
                )
            )
        }
    }

    fun removeRule(id: String) {
        _rules.removeAll { it.id == id }
    }

    fun toggleRule(id: String) {
        val idx = _rules.indexOfFirst { it.id == id }
        if (idx >= 0) {
            _rules[idx] = _rules[idx].copy(isActive = !_rules[idx].isActive)
        }
    }

    fun isUrlBlocked(url: String): Boolean {
        if (!isFocusMode) return false
        return _rules.any { it.matches(url) }
    }

    fun incrementBlockedAttempts() {
        blockedAttempts++
    }

    fun recordVisit(url: String) {
        totalVisits++
        sessionPages++
        val host = url
            .removePrefix("https://").removePrefix("http://")
            .split("/").firstOrNull()?.split(":")?.firstOrNull() ?: url
        val now = currentTimeMillis()
        val entry = VisitEntry(url = url, host = host, timestamp = now)
        _visitLog.add(entry)

        val idx = _domainStats.indexOfFirst { it.host == host }
        if (idx >= 0) {
            val existing = _domainStats[idx]
            _domainStats[idx] = existing.copy(
                visitCount = existing.visitCount + 1,
                lastVisit = now
            )
        } else {
            _domainStats.add(DomainStats(host = host, visitCount = 1, lastVisit = now))
        }
    }

    companion object {
        val DEFAULT_BLOCKED_DOMAINS = listOf(
            "youtube.com" to "YouTube",
            "reddit.com" to "Reddit",
            "twitter.com" to "Twitter",
            "x.com" to "X / Twitter",
            "instagram.com" to "Instagram",
            "facebook.com" to "Facebook",
            "tiktok.com" to "TikTok",
            "netflix.com" to "Netflix",
            "twitch.tv" to "Twitch",
            "discord.com" to "Discord",
            "*.x.com" to "Subdominios de X",
        )
    }
}
