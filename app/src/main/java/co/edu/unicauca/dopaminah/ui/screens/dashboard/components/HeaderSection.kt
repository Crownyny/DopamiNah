package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.ui.components.AppIcon
import co.edu.unicauca.dopaminah.ui.screens.dashboard.DashboardContent
import co.edu.unicauca.dopaminah.ui.theme.*

@Composable
fun HeaderSection(gamificationStats: UserGamificationStats) {
    val colorScheme = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(colorScheme.primaryContainer)
            .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Spacer(Modifier.height(16.dp))
                    Row {
                        Text(
                            text = stringResource(R.string.app_halfname1),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.app_halfname2),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = extended.brandOrange
                        )
                    }
                    Text(
                        text = stringResource(R.string.app_description),
                        fontSize = 14.sp,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // App icon
                Box(
                    Modifier.padding(start = 16.dp, end = 16.dp)
                ) { AppIcon(size = 84)}


            }



            // Streak Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = extended.brandOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val streakIcon = if (gamificationStats.currentPoints >= 5) "🔥" else "🌱"
                            Text(streakIcon, fontSize = 40.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text(
                                    stringResource(R.string.dashboard_streak),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        gamificationStats.currentPoints.toString(),
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        stringResource(R.string.dashboard_streaks_days),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                    )
                                }
                            }
                        }

                        // Level Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.dashboard_level), color = Color.White, fontSize = 12.sp)
                                Text("${gamificationStats.level}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(vertical = 12.dp))

                    val streakMessage = when {
                        gamificationStats.currentPoints <= 1 -> stringResource(R.string.streak_lvl_0)
                        gamificationStats.currentPoints <= 4 -> stringResource(R.string.streak_lvl_1)
                        gamificationStats.currentPoints <= 10 -> stringResource(R.string.streak_lvl_2)
                        gamificationStats.currentPoints <= 30 -> stringResource(R.string.streak_lvl_3)
                        else -> stringResource(R.string.streak_lvl_4)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = extended.warningYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            streakMessage,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DopamiNahTheme {
        HeaderSection(
            gamificationStats = UserGamificationStats(
                currentPoints = 15,
                level = 2,
            )
        )
    }
}
