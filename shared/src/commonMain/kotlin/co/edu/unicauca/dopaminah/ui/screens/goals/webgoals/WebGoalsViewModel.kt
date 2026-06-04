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
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(WebGoalsState())
    val state: StateFlow<WebGoalsState> = _state.asStateFlow()

    private val goals = mutableListOf<WebGoal>()
    private val domainTime = mutableMapOf<String, Int>()
    private var timerJob: Job? = null

    init {
        _state.value = _state.value.copy(isLoading = false)
        startTimer()
    }

    fun addGoal(url: String, timeLimitMinutes: Int) {
        val domain = url
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")
            .trimEnd('/')
            .lowercase()
        val id = "wg_${goals.size}_${currentTimeMillis()}"
        goals.add(WebGoal(id, domain, url, timeLimitMinutes, true))
        domainTime[domain] = 0
        rebuildState()
    }

    fun deleteGoal(id: String) {
        val goal = goals.find { it.id == id } ?: return
        goals.removeAll { it.id == id }
        domainTime.remove(goal.domain)
        rebuildState()
    }

    fun toggleGoal(id: String) {
        val index = goals.indexOfFirst { it.id == id }
        if (index != -1) {
            goals[index] = goals[index].copy(isActive = !goals[index].isActive)
            rebuildState()
        }
    }

    fun editGoal(id: String, newLimitMinutes: Int) {
        val index = goals.indexOfFirst { it.id == id }
        if (index != -1) {
            goals[index] = goals[index].copy(dailyTimeLimitMinutes = newLimitMinutes)
            rebuildState()
        }
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                delay(10_000L)
                simulateTimeTick()
            }
        }
    }

    private fun simulateTimeTick() {
        val activeDomains = goals.filter { it.isActive }.map { it.domain }
        activeDomains.forEach { domain ->
            val current = domainTime[domain] ?: 0
            domainTime[domain] = current + 1
        }
        rebuildState()
    }

    private fun rebuildState() {
        val uiModels = goals.map { goal ->
            val spentSecs = domainTime[goal.domain] ?: 0
            val spentMinutes = spentSecs / 60
            val limitMinutes = goal.dailyTimeLimitMinutes
            val percent = if (limitMinutes > 0) spentMinutes.toFloat() / limitMinutes else 0f
            val blocked = spentMinutes >= limitMinutes && goal.isActive

            WebGoalUiModel(
                id = goal.id,
                domain = goal.domain,
                displayUrl = goal.displayUrl,
                dailyTimeLimitMinutes = limitMinutes,
                todaySpentMinutes = spentMinutes,
                progressPercent = percent,
                isBlocked = blocked,
                remainingMinutes = (limitMinutes - spentMinutes).coerceAtLeast(0),
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
