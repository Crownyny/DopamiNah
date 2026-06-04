package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

import co.edu.unicauca.dopaminah.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WebGoalsViewModel {
    var onSyncOut: ((List<WebGoalUiModel>) -> Unit)? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(WebGoalsState())
    val state: StateFlow<WebGoalsState> = _state.asStateFlow()

    private val goals = mutableListOf<WebGoal>()
    private val domainAccumulatedMinutes = mutableMapOf<String, Int>()
    private val domainActiveStartTime = mutableMapOf<String, Long>()
    private var currentDomain: String? = null
    private var timerJob: Job? = null

    init {
        _state.value = _state.value.copy(isLoading = false)
        startTimer()
    }

    fun addGoal(url: String, timeLimitMinutes: Int) {
        val domain = extractDomain(url)
        val id = "wg_${goals.size}_${currentTimeMillis()}"
        goals.add(WebGoal(id, domain, url, timeLimitMinutes, true))
        domainAccumulatedMinutes[domain] = 0
        if (domain == currentDomain) {
            domainActiveStartTime[domain] = currentTimeMillis()
        }
        rebuildState()
        notifyOutboundSync()
    }

    fun deleteGoal(id: String) {
        val goal = goals.find { it.id == id } ?: return
        goals.removeAll { it.id == id }
        domainAccumulatedMinutes.remove(goal.domain)
        domainActiveStartTime.remove(goal.domain)
        if (currentDomain == goal.domain) currentDomain = null
        rebuildState()
        notifyOutboundSync()
    }

    fun toggleGoal(id: String) {
        val index = goals.indexOfFirst { it.id == id }
        if (index != -1) {
            val goal = goals[index]
            val wasActive = goal.isActive
            goals[index] = goal.copy(isActive = !wasActive)

            if (wasActive) {
                pauseDomain(goal.domain)
            } else if (goal.domain == currentDomain) {
                domainActiveStartTime[goal.domain] = currentTimeMillis()
            }
            rebuildState()
            notifyOutboundSync()
        }
    }

    fun editGoal(id: String, newLimitMinutes: Int) {
        val index = goals.indexOfFirst { it.id == id }
        if (index != -1) {
            goals[index] = goals[index].copy(dailyTimeLimitMinutes = newLimitMinutes)
            rebuildState()
            notifyOutboundSync()
        }
    }

    private fun notifyOutboundSync() {
        onSyncOut?.invoke(_state.value.webGoals)
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false)
    }

    fun notifyVisit(url: String) {
        val domain = extractDomain(url)
        if (domain == currentDomain) return
        pauseCurrentDomain()
        currentDomain = domain
        if (goals.any { it.domain == domain && it.isActive }) {
            if (domainActiveStartTime[domain] == null) {
                domainActiveStartTime[domain] = currentTimeMillis()
            }
        }
        rebuildState()
    }

    fun notifyStop() {
        pauseCurrentDomain()
        currentDomain = null
        rebuildState()
    }

    fun isDomainBlocked(url: String): Boolean {
        val domain = extractDomain(url)
        return state.value.webGoals.any { goal ->
            goal.isBlocked && (goal.domain == domain || domain.endsWith(".${goal.domain}"))
        }
    }

    private fun extractDomain(url: String): String {
        return url
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("ftp://")
            .split("/").firstOrNull()
            ?.split(":")?.firstOrNull()
            ?.removePrefix("www.")
            ?.lowercase() ?: url.lowercase()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                delay(1_000L)
                rebuildState()
            }
        }
    }

    private fun pauseDomain(domain: String) {
        domainActiveStartTime[domain]?.let { startTime ->
            val elapsed = ((currentTimeMillis() - startTime) / 60000).toInt()
            domainAccumulatedMinutes[domain] = (domainAccumulatedMinutes[domain] ?: 0) + elapsed
            domainActiveStartTime.remove(domain)
        }
    }

    private fun pauseCurrentDomain() {
        currentDomain?.let { pauseDomain(it) }
    }

    private fun spentMinutesFor(domain: String): Int {
        val accumulated = domainAccumulatedMinutes[domain] ?: return 0
        val startTime = domainActiveStartTime[domain] ?: return accumulated
        val sessionMinutes = ((currentTimeMillis() - startTime) / 60000).toInt()
        return accumulated + sessionMinutes
    }

    private fun rebuildState() {
        val uiModels = goals.map { goal ->
            val spentMinutes = spentMinutesFor(goal.domain)
            val limitMinutes = goal.dailyTimeLimitMinutes
            val percent = if (limitMinutes > 0) spentMinutes.toFloat() / limitMinutes else 0f
            val blocked = if (limitMinutes == 0) goal.isActive else spentMinutes >= limitMinutes && goal.isActive

            WebGoalUiModel(
                id = goal.id,
                domain = goal.domain,
                displayUrl = goal.displayUrl,
                dailyTimeLimitMinutes = limitMinutes,
                todaySpentMinutes = spentMinutes,
                progressPercent = percent,
                isBlocked = blocked,
                remainingMinutes = if (limitMinutes == 0) 0 else (limitMinutes - spentMinutes).coerceAtLeast(0),
                isActive = goal.isActive
            )
        }

        _state.value = _state.value.copy(
            webGoals = uiModels,
            isLoading = false,
            activeBlocks = uiModels.count { it.isBlocked },
            totalGoals = uiModels.size
        )
    }
}
