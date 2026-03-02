package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.ui.theme.*
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils.calculateDiffText
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils.calculateTimeDiff
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils.formatUsageTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UsageSummaryCarousel(
    dailyUnlocks: Int,
    yesterdayUnlocks: Int,
    totalDailyUsageMs: Long,
    yesterdayUsageMs: Long? = null // Optional, if we want to add comparison later
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll effect: pause when user is interacting
    val isScrollInProgress = pagerState.isScrollInProgress
    LaunchedEffect(isScrollInProgress) {
        if (!isScrollInProgress) {
            while (true) {
                delay(5000) // Wait for 5 seconds
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    // First Card: Unlocks
                    StatCard(
                        title = stringResource(R.string.dashboard_unlocks),
                        icon = Icons.Default.Lock,
                        mainValue = "$dailyUnlocks",
                        subtext = stringResource(R.string.dashboard_unlocks_times),
                        diffText = calculateDiffText(dailyUnlocks, yesterdayUnlocks),
                        diffColor = if (dailyUnlocks - yesterdayUnlocks <= 0) SuccessGreen else DopaminahRedText
                    )
                }
                1 -> {
                    // Second Card: Screen Time
                    StatCard(
                        title = "Uso de Pantalla", // Consider moving to strings.xml later if preferred
                        icon = Icons.Default.Schedule,
                        mainValue = formatUsageTime(totalDailyUsageMs),
                        subtext = "hoy",
                        diffText = if (yesterdayUsageMs != null) calculateTimeDiff(totalDailyUsageMs, yesterdayUsageMs) else "Datos calculándose...",
                        diffColor = Color.White // Neutral color or success if improved
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pager indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(2) { iteration ->
                val color = if (pagerState.currentPage == iteration) DopaminahRedText else Color.LightGray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = iteration,
                                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                                )
                            }
                        }
                        .size(if (pagerState.currentPage == iteration) 24.dp else 12.dp, 6.dp)
                )
            }
        }
    }
}
