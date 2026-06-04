package co.edu.unicauca.dopaminah.ui.screens.focusbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.currentTimeMillis
import co.edu.unicauca.dopaminah.ui.icons.*
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalUiModel
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalsState
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalsViewModel
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import kotlinx.coroutines.delay

@Composable
fun WebStatsScreen(
    navRepository: WebNavigationRepository? = null,
    goalsViewModel: WebGoalsViewModel? = null
) {
    val repo = navRepository ?: remember { WebNavigationRepository() }
    val goalsState by goalsViewModel?.state?.collectAsState() ?: remember { mutableStateOf(null) }
    val goals = goalsState?.webGoals ?: emptyList()
    val totalTodayMinutes = goals.sumOf { it.todaySpentMinutes }
    var now by remember { mutableStateOf(currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = currentTimeMillis()
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(DopaminahPurpleDark)
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = "Navegaci\u00f3n",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Estad\u00edsticas de tu actividad en el navegador",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            item {
                TodayCard(
                    totalMinutes = totalTodayMinutes,
                    goalCount = goals.size,
                    now = now
                )
            }

            item {
                TopDomainsCard(goals = goals)
            }

            item {
                BlockedStatsCard(repo = repo, goalsState = goalsState)
            }
        }
    }
}

@Composable
private fun TodayCard(totalMinutes: Int, goalCount: Int, now: Long) {
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    val durationText = when {
        hours > 0 -> "${hours}h ${mins}m"
        mins > 0 -> "${mins}m"
        else -> "0m"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    LucideTimer, contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tiempo hoy",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = durationText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DopaminahPurpleDark
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(LucideTarget, "${goalCount}", "Sitios")
                StatItem(LucideGlobe, "${totalMinutes}", "Minutos")
                StatItem(LucideChevronRight, if (totalMinutes > 0) "Activo" else "Inactivo", "Seguimiento")
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TopDomainsCard(goals: List<WebGoalUiModel>) {
    val sorted = goals.sortedByDescending { it.todaySpentMinutes }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    LucideTrendingUp, contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Sitios con seguimiento",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            if (sorted.isEmpty()) {
                Text(
                    "A\u00fan no has agregado sitios para controlar.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                sorted.take(10).forEachIndexed { idx, goal ->
                    if (idx > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${idx + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(24.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            goal.domain,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            formatMinutes(goal.todaySpentMinutes),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    if (minutes < 60) return "${minutes}m"
    val h = minutes / 60
    val r = minutes % 60
    return if (r > 0) "${h}h ${r}m" else "${h}h"
}

@Composable
private fun BlockedStatsCard(repo: WebNavigationRepository, goalsState: WebGoalsState?) {
    val activeGoals = goalsState?.webGoals?.count { it.isActive } ?: 0
    val blockedGoals = goalsState?.webGoals?.count { it.isBlocked } ?: 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    LucideShieldAlert, contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Bloqueos",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${blockedGoals}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "Sitios bloqueados",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${activeGoals}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DopaminahPurpleDark
                    )
                    Text(
                        "Limites activos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${repo.blockedAttempts}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Intentos bloqueados",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
