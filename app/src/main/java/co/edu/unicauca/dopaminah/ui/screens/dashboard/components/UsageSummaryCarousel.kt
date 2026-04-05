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
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.domain.usecase.AppLimitCardInfo
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
    appLimitCards: List<AppLimitCardInfo> = emptyList(),
    yesterdayUsageMs: Long? = null 
) {
    val totalPages = 2 + appLimitCards.size
    val pagerState = rememberPagerState(pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            while (true) {
                delay(5000)
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
                    val isImproved = dailyUnlocks - yesterdayUnlocks <= 0
                    StatCard(
                        title = stringResource(R.string.dashboard_unlocks),
                        icon = Icons.Default.Lock,
                        mainValue = "$dailyUnlocks",
                        subtext = stringResource(R.string.dashboard_unlocks_times),
                        diffText = calculateDiffText(dailyUnlocks, yesterdayUnlocks),
                        diffColor = if (isImproved) SuccessGreenLight else DangerRed,
                        diffBgColor = if (isImproved) SuccessGreenDark else DangerRed.copy(alpha = 0.2f),
                        containerColor = StatCardDark,
                        contentColor = TextPrimaryDark,
                        accentColor = StatCardPink
                    )
                }
                1 -> {
                    // Second Card: Screen Time
                    StatCard(
                        title = stringResource(R.string.dashboard_screen_time),
                        icon = Icons.Default.Schedule,
                        mainValue = formatUsageTime(totalDailyUsageMs),
                        subtext = stringResource(R.string.dashboard_today),
                        diffText = if (yesterdayUsageMs != null) calculateTimeDiff(totalDailyUsageMs, yesterdayUsageMs) else stringResource(R.string.dashboard_calculating),
                        diffColor = TextSecondaryDark,
                        diffBgColor = Color.White.copy(alpha = 0.05f),
                        containerColor = StatCardDark,
                        contentColor = TextPrimaryDark,
                        accentColor = StatCardPink
                    )
                }
                else -> {
                    // Dynamic App Limit Cards
                    val cardInfo = appLimitCards[page - 2]
                    val ratio = if (cardInfo.timeLimitMs > 0) cardInfo.timeUsedMs.toFloat() / cardInfo.timeLimitMs else 1f
                    
                    val (containerCol, contentCol, accentCol) = when {
                        ratio >= 1.0f -> Triple(DangerRed, Color.White, Color.White)
                        ratio >= 0.7f -> Triple(WarningYellow, WarningYellowDark, WarningYellowAccent)
                        else -> Triple(SuccessGreen, Color.White, Color.White)
                    }

                    StatCard(
                        title = cardInfo.appName,
                        icon = Icons.Default.Smartphone,
                        mainValue = formatUsageTime(cardInfo.timeUsedMs),
                        subtext = stringResource(R.string.dashboard_today),
                        diffText = stringResource(R.string.dashboard_limit_prefix, formatUsageTime(cardInfo.timeLimitMs)),
                        diffColor = contentCol,
                        containerColor = containerCol,
                        contentColor = contentCol,
                        accentColor = accentCol
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(totalPages) { iteration ->
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
