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
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsTab

@Composable
fun PeakUsageChartCard(
    hourlyUsage: List<Float>,
    selectedTab: StatsTab,
    modifier: Modifier = Modifier
) {
    val periodLabel = if (selectedTab == StatsTab.WEEKLY) "sem." else "mes"
    val maxMinutes = hourlyUsage.maxOfOrNull { it }?.coerceAtLeast(1f) ?: 60f
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Horas Pico de Uso", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "prom. / día ($periodLabel)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.BottomCenter) {
                if (hourlyUsage.isEmpty()) {
                    Text(text = "Sin datos disponibles", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.align(Alignment.Center))
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                        hourlyUsage.forEachIndexed { _, minutes ->
                            PeakBar(minutes = minutes, maxMinutes = maxMinutes, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("00h", "06h", "12h", "18h", "23h").forEach { label ->
                    Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun PeakBar(minutes: Float, maxMinutes: Float, modifier: Modifier = Modifier) {
    val fraction = (minutes / maxMinutes).coerceIn(0.02f, 1f)
    var animate by remember { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (animate) fraction else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bar_anim"
    )
    LaunchedEffect(Unit) { animate = true }
    val barColor = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    )
    Box(
        modifier = modifier
            .fillMaxHeight(animatedFraction)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(barColor)
    )
}
