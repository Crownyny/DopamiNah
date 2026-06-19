package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val maxHours = appUsageData.maxOfOrNull { it.averageHours }?.coerceAtLeast(0.1f) ?: 1f
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors =         CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Apps más usadas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (selectedTab == StatsTab.WEEKLY) "sem." else "mes",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (appUsageData.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                    Text("Sin datos", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                appUsageData.forEach { entry ->
                    AppUsageBar(
                        appName = entry.appName,
                        hours = entry.averageHours,
                        maxHours = maxHours
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun AppUsageBar(
    appName: String,
    hours: Float,
    maxHours: Float
) {
    val fraction = (hours / maxHours).coerceIn(0f, 1f)
    var animate by remember { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (animate) fraction else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "app_bar"
    )
    LaunchedEffect(Unit) { animate = true }
    val hoursDisplay = hours.toInt()
    val minutesDisplay = ((hours - hoursDisplay) * 60).toInt()
    val timeLabel = if (hoursDisplay > 0) "${hoursDisplay}h ${minutesDisplay}m" else "${minutesDisplay}m"
    val brush = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
    )
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)))
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedFraction)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush)
                .align(Alignment.Start)
                .padding(end = 6.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(text = timeLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
