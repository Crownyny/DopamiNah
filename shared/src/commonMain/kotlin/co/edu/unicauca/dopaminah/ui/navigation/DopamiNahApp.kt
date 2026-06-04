package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.screens.dashboard.DashboardScreen
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.StatsScreen
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.GoalsScreen
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalsScreen
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.AchievementsScreen
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.focusbrowser.WebNavigationRepository
import co.edu.unicauca.dopaminah.ui.screens.focusbrowser.WebStatsScreen
import co.edu.unicauca.dopaminah.ui.screens.settings.SettingsScreen
import co.edu.unicauca.dopaminah.ui.screens.onboarding.OnboardingPermissionScreen
import co.edu.unicauca.dopaminah.ui.screens.webview.WebViewScreen
import co.edu.unicauca.dopaminah.ui.icons.LucideHouse
import co.edu.unicauca.dopaminah.ui.icons.LucideChartColumn
import co.edu.unicauca.dopaminah.ui.icons.LucideTarget
import co.edu.unicauca.dopaminah.ui.icons.LucideGlobe
import co.edu.unicauca.dopaminah.ui.icons.LucideAward
import co.edu.unicauca.dopaminah.ui.icons.LucideSettings as LucideSettingsIcon
import co.edu.unicauca.dopaminah.SyncBridge

enum class AppTab(val route: String, val title: String) {
    DASHBOARD("dashboard", "Inicio"),
    STATS("stats", "Stats"),
    GOALS("goals", "Metas"),
    WEB("navegacion", "Navegación"),
    ACHIEVEMENTS("achievements", "Logros"),
    SETTINGS("settings", "Ajustes")
}

@Composable
fun DopamiNahApp(
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null,
    navRepository: WebNavigationRepository? = null,
    hiddenTabs: Set<AppTab> = emptySet(),
    useWebGoals: Boolean = false,
    onSyncGoalsToExtension: ((String) -> Unit)? = null
) {
    val permissionState = LocalPermissionState.current

    DopamiNahTheme(darkTheme = darkMode) {
        if (!permissionState.hasUsagePermission) {
            OnboardingPermissionScreen(
                onPermissionGranted = {
                    permissionState.onPermissionGranted()
                },
                onRequestUsagePermission = permissionState.onRequestUsagePermission,
                onRequestNotificationPermission = permissionState.onRequestNotificationPermission
            )
        } else {
            MainContent(
                darkMode = darkMode,
                onDarkModeChange = onDarkModeChange,
                dashboardViewModel = dashboardViewModel,
                statsViewModel = statsViewModel,
                goalsViewModel = goalsViewModel,
                achievementsViewModel = achievementsViewModel,
                navRepository = navRepository,
                hiddenTabs = hiddenTabs,
                useWebGoals = useWebGoals,
                onSyncGoalsToExtension = onSyncGoalsToExtension
            )
        }
    }
}

@Composable
private fun MainContent(
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null,
    navRepository: WebNavigationRepository? = null,
    hiddenTabs: Set<AppTab> = emptySet(),
    useWebGoals: Boolean = false,
    onSyncGoalsToExtension: ((String) -> Unit)? = null
) {
    val visibleTabs = AppTab.entries.filter { it !in hiddenTabs }
    val firstVisibleTab = visibleTabs.firstOrNull() ?: AppTab.SETTINGS
    var selectedTab by remember { mutableStateOf(firstVisibleTab) }
    var pendingUrl by remember { mutableStateOf<String?>(null) }
    val webGoalsViewModel = remember {
        if (useWebGoals) {
            WebGoalsViewModel().also { vm ->
                SyncBridge.onIncomingSync = { domain, minutes ->
                    if (!vm.state.value.webGoals.any { it.domain == domain }) {
                        vm.addGoal(domain, minutes)
                    }
                }
                SyncBridge.onDomainTimeSync = { domain, spentMinutes ->
                    vm.setAccumulatedMinutes(domain, spentMinutes)
                }
                vm.onSyncOut = { uiModels ->
                    val json = buildString {
                        append("[")
                        uiModels.forEachIndexed { i, g ->
                            if (i > 0) append(",")
                            val d = g.domain.replace("\\", "\\\\").replace("\"", "\\\"")
                            val u = g.displayUrl.replace("\\", "\\\\").replace("\"", "\\\"")
                            append("""{"domain":"$d","displayUrl":"$u","timeLimitMinutes":${g.dailyTimeLimitMinutes},"isActive":${g.isActive}}""")
                        }
                        append("]")
                    }
                    onSyncGoalsToExtension?.invoke(json)
                }
            }
        } else null
    }

    LaunchedEffect(selectedTab, hiddenTabs) {
        if (selectedTab in hiddenTabs) {
            selectedTab = firstVisibleTab
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    if (AppTab.DASHBOARD !in hiddenTabs) {
                        NavigationBarItem(
                            icon = { Icon(LucideHouse, contentDescription = "Inicio") },
                            label = { Text("Inicio") },
                            selected = selectedTab == AppTab.DASHBOARD,
                            onClick = { selectedTab = AppTab.DASHBOARD }
                        )
                    }
                    if (AppTab.STATS !in hiddenTabs) {
                        NavigationBarItem(
                            icon = { Icon(LucideChartColumn, contentDescription = "Stats") },
                            label = { Text("Stats") },
                            selected = selectedTab == AppTab.STATS,
                            onClick = { selectedTab = AppTab.STATS }
                        )
                    }
                    if (AppTab.GOALS !in hiddenTabs) {
                        NavigationBarItem(
                            icon = { Icon(LucideTarget, contentDescription = "Metas") },
                            label = { Text("Metas") },
                            selected = selectedTab == AppTab.GOALS,
                            onClick = { selectedTab = AppTab.GOALS }
                        )
                    }
                    if (AppTab.WEB !in hiddenTabs) {
                        NavigationBarItem(
                            icon = { Icon(LucideGlobe, contentDescription = "Navegación") },
                            label = { Text("Navegación") },
                            selected = selectedTab == AppTab.WEB,
                            onClick = { selectedTab = AppTab.WEB }
                        )
                    }
                    if (AppTab.ACHIEVEMENTS !in hiddenTabs) {
                        NavigationBarItem(
                            icon = { Icon(LucideAward, contentDescription = "Logros") },
                            label = { Text("Logros") },
                            selected = selectedTab == AppTab.ACHIEVEMENTS,
                            onClick = { selectedTab = AppTab.ACHIEVEMENTS }
                        )
                    }
                    NavigationBarItem(
                        icon = { Icon(LucideSettingsIcon, contentDescription = "Ajustes") },
                        label = { Text("Ajustes") },
                        selected = selectedTab == AppTab.SETTINGS,
                        onClick = { selectedTab = AppTab.SETTINGS }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedTab) {
                    AppTab.DASHBOARD -> DashboardScreen(viewModel = dashboardViewModel)
                    AppTab.STATS -> StatsScreen(viewModel = statsViewModel)
                    AppTab.GOALS -> {
                        if (useWebGoals) {
                            WebGoalsScreen(viewModel = webGoalsViewModel)
                        } else {
                            GoalsScreen(viewModel = goalsViewModel)
                        }
                    }
                    AppTab.WEB -> WebStatsScreen(navRepository = navRepository, goalsViewModel = webGoalsViewModel)
                    AppTab.ACHIEVEMENTS -> AchievementsScreen(viewModel = achievementsViewModel)
                    AppTab.SETTINGS -> SettingsScreen(
                        darkMode = darkMode,
                        onDarkModeChange = onDarkModeChange,
                        onOpenUrl = { pendingUrl = it },
                        navRepository = navRepository
                    )
                }
            }
        }

        pendingUrl?.let { url ->
            WebViewScreen(
                url = url,
                onClose = {
                    navRepository?.endSession()
                    webGoalsViewModel?.notifyStop()
                    pendingUrl = null
                },
                shouldCheckBlock = navRepository?.let { repo ->
                    { urlToCheck ->
                        val repoBlocked = repo.isUrlBlocked(urlToCheck)
                        val goalBlocked = webGoalsViewModel?.isDomainBlocked(urlToCheck) == true
                        val isBlocked = repoBlocked || goalBlocked
                        if (isBlocked) {
                            repo.incrementBlockedAttempts()
                        } else {
                            repo.recordVisit(urlToCheck)
                            webGoalsViewModel?.notifyVisit(urlToCheck)
                        }
                        isBlocked
                    }
                }
            )
        }

        LaunchedEffect(pendingUrl) {
            if (pendingUrl != null) {
                navRepository?.startSession()
                navRepository?.recordVisit(pendingUrl!!)
                webGoalsViewModel?.notifyVisit(pendingUrl!!)
            }
        }
    }
}
