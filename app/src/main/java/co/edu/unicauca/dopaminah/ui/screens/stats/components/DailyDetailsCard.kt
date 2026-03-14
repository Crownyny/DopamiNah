package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.ui.icons.LucideAward
import co.edu.unicauca.dopaminah.ui.icons.LucideCalendar
import co.edu.unicauca.dopaminah.ui.icons.LucideChevronLeft
import co.edu.unicauca.dopaminah.ui.icons.LucideChevronRight
import co.edu.unicauca.dopaminah.ui.theme.extendedColors
import co.edu.unicauca.dopaminah.ui.icons.LucideSunrise
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer

import co.edu.unicauca.dopaminah.domain.repository.DailyDetailStats
import co.edu.unicauca.dopaminah.ui.icons.LucideSmartphone

@Composable
fun DailyDetailsCard(
    details: DailyDetailStats?,
    selectedDayOffset: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSelectDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Format total time
    val totalMillis = details?.totalTimeMillis ?: 0L
    val totalH = (totalMillis / 3_600_000).toInt()
    val totalM = ((totalMillis % 3_600_000) / 60_000).toInt()
    val totalTimeStr = if (totalH > 0) "${totalH}h ${totalM}m" else "${totalM}m"

    // Format avg session
    val avgMins = details?.avgSessionMinutes ?: 0
    val avgSessionStr = if (avgMins >= 60) "${avgMins / 60}h ${avgMins % 60}m" else "${avgMins}m"
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary, // Purple
                                MaterialTheme.extendedColors.dangerRed  // Pink/Red
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onPreviousDay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideChevronLeft,
                            contentDescription = "Previous Day",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectDay() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = LucideCalendar,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.stats_day_details),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = details?.dateLabel ?: "--",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (selectedDayOffset > 0) 0.2f else 0.08f))
                            .clickable(enabled = selectedDayOffset > 0) { onNextDay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideChevronRight,
                            contentDescription = "Next Day",
                            tint = Color.White.copy(alpha = if (selectedDayOffset > 0) 1f else 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Grid Content
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Row of Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GridItem(
                        icon = LucideSunrise,
                        title = "Primera vez",
                        mainValue = details?.firstUseTime ?: "--",
                        subValue = null,
                        backgroundColor = MaterialTheme.extendedColors.brandOrange,
                        modifier = Modifier.weight(1f)
                    )
                    GridItem(
                        icon = LucideTimer,
                        title = "Promedio por sesión",
                        mainValue = avgSessionStr,
                        subValue = null,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Row of Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GridItem(
                        icon = LucideAward,
                        title = "App más usada",
                        mainValue = details?.mostUsedAppName ?: "--",
                        subValue = details?.mostUsedAppTime,
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    GridItem(
                        icon = LucideSmartphone,
                        title = "Desbloqueos",
                        mainValue = "${details?.unlocks ?: "--"}",
                        subValue = "veces",
                        backgroundColor = MaterialTheme.extendedColors.successGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Total Time Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), // Purple thin background
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = LucideTimer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = stringResource(R.string.stats_total_time),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        Text(
                            text = totalTimeStr,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    mainValue: String,
    subValue: String?,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = mainValue,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (subValue != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subValue,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
