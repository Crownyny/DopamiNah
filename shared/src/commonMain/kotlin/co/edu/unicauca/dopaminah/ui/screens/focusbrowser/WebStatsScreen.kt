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
                    text = "Navegaci\u00f3n Web",
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
                SessionCard(
                    repo = repo,
                    now = now
                )
            }

            item {
                TopDomainsCard(repo = repo)
            }

            item {
                BlockedStatsCard(repo = repo, goalsState = goalsState)
            }
        }
    }
}

@Composable
private fun SessionCard(
    repo: WebNavigationRepository,
    now: Long
) {
    val durationMs = if (repo.sessionStartTime > 0L) now - repo.sessionStartTime else 0L
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / 60000) % 60
    val hours = durationMs / 3600000
    val durationText = when {
        hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
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
                    "Sesi\u00f3n actual",
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
                StatItem(LucideChevronRight, "${repo.sessionPages}", "P\u00e1ginas")
                StatItem(LucideGlobe, "${repo.totalVisits}", "Total visitas")
                StatItem(
                    if (repo.isFocusMode) LucideShieldAlert else LucideShield,
                    if (repo.isFocusMode) "Activo" else "Inactivo",
                    "Modo Enfoque"
                )
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
private fun TopDomainsCard(repo: WebNavigationRepository) {
    val sorted = repo.domainStats.sortedByDescending { it.visitCount }

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
                    "Sitios m\u00e1s visitados",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            if (sorted.isEmpty()) {
                Text(
                    "A\u00fan no has visitado ning\u00fan sitio.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                sorted.take(10).forEachIndexed { idx, stat ->
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
                            stat.host,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${stat.visitCount}",
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
