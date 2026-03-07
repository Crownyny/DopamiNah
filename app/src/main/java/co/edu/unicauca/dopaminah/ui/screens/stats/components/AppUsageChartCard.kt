package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.AppUsageEntry
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsTab

@Composable
fun AppUsageChartCard(
    appUsageData: List<AppUsageEntry>,
    selectedTab: StatsTab,
    modifier: Modifier = Modifier
) {
    val periodLabel = if (selectedTab == StatsTab.WEEKLY) "sem." else "mes"
    val maxHours = appUsageData.maxOfOrNull { it.averageHours }?.coerceAtLeast(0.1f) ?: 1f

    // X-axis tick values (0, max/2, max)
    val xTicks = listOf(0f, maxHours / 2f, maxHours)

    val barColors = listOf(
        Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA))),
        Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFFFB7185))),
        Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))),
        Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF6EE7B7))),
        Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFCD34D))),
        Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA5B4FC))),
        Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFFF9A8D4))),
        Brush.horizontalGradient(listOf(Color(0xFF14B8A6), Color(0xFF5EEAD4))),
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(24.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uso por Aplicación",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "prom. / día ($periodLabel)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (appUsageData.isEmpty()) {
                Text(
                    text = "Sin datos disponibles",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            } else {
                // X-axis ticks (drawn above scrollable area)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 104.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    xTicks.forEach { tick ->
                        val h = tick.toInt()
                        val m = ((tick - h) * 60).toInt()
                        val label = if (h > 0) "${h}h${if (m > 0) " ${m}m" else ""}" else "${m}m"
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable bar chart area
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    appUsageData.forEachIndexed { index, entry ->
                        AppBarRow(
                            appName = entry.appName,
                            hours = entry.averageHours,
                            maxHours = maxHours,
                            brush = barColors[index % barColors.size]
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun AppBarRow(
    appName: String,
    hours: Float,
    maxHours: Float,
    brush: Brush
) {
    val fraction = (hours / maxHours).coerceIn(0f, 1f)

    var animate by remember { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (animate) fraction else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "bar_anim"
    )
    LaunchedEffect(Unit) { animate = true }

    val h = hours.toInt()
    val m = ((hours - h) * 60).toInt()
    val timeLabel = if (h > 0) "${h}h ${m}m" else "${m}m"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App name label
        Text(
            text = appName,
            modifier = Modifier.width(96.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Bar + time label
        Box(modifier = Modifier.weight(1f)) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            )
            // Animated fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush)
            )
            // Time label inside bar
            Text(
                text = timeLabel,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
