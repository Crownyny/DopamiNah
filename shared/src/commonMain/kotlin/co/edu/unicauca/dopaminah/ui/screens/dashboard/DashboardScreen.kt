package co.edu.unicauca.dopaminah.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.usecase.AppLimitCardInfo
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.UsageSummaryCarousel
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.HeaderSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.components.MostUsedAppsSection
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel

@Composable
/** Home dashboard screen showing gamification stats, usage summary carousel, most-used apps, and app-limit cards. */
fun DashboardScreen(
    viewModel: DashboardViewModel? = null
) {
    val vm = viewModel ?: remember { DashboardViewModel.createEmpty() }

    val gamificationStats by vm.gamificationState.collectAsState()
    val dailyUnlocks by vm.dailyUnlocks.collectAsState()
    val yesterdayUnlocks by vm.yesterdayUnlocks.collectAsState()
    val totalDailyUsageMs by vm.totalDailyUsageMs.collectAsState()
    val hasPermission by vm.hasUsagePermission.collectAsState()
    val dailyUsageStats by vm.dailyUsageStats.collectAsState()
    val appLimitCards by vm.appLimitCards.collectAsState()

    DashboardContent(
        gamificationStats = gamificationStats,
        dailyUnlocks = dailyUnlocks,
        yesterdayUnlocks = yesterdayUnlocks,
        totalDailyUsageMs = totalDailyUsageMs,
        hasPermission = hasPermission,
        dailyUsageStats = dailyUsageStats,
        appLimitCards = appLimitCards
    )
}

@Composable
fun DashboardContent(
    gamificationStats: UserGamificationStats,
    dailyUnlocks: Int,
    yesterdayUnlocks: Int,
    totalDailyUsageMs: Long,
    hasPermission: Boolean,
    dailyUsageStats: List<AppUsageSummary>,
    appLimitCards: List<AppLimitCardInfo>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HeaderSection(gamificationStats = gamificationStats)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
        ) {
            item {
                UsageSummaryCarousel(
                    dailyUnlocks = dailyUnlocks,
                    yesterdayUnlocks = yesterdayUnlocks,
                    totalDailyUsageMs = totalDailyUsageMs,
                    appLimitCards = appLimitCards
                )
                Spacer(modifier = Modifier.height(24.dp))
                MostUsedAppsSection(dailyUsageStats = dailyUsageStats, hasPermission = hasPermission)
            }
        }
    }
}
