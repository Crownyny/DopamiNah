package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.*
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats

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
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Row {
                        Text("Dopami", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onPrimaryContainer)
                        Text("Nah", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = extended.brandOrange)
                    }
                    Text("Tu asistente de bienestar digital", fontSize = 14.sp, color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = extended.brandOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val streakIcon = if (gamificationStats.streak >= 5) "\uD83D\uDD25" else "\uD83C\uDF31"
                            Text(streakIcon, fontSize = 40.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text("RACHA ACTUAL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(gamificationStats.streak.toString(), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(" días consecutivos", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nivel", color = Color.White, fontSize = 12.sp)
                                Text("${gamificationStats.level}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(vertical = 12.dp))
                    val streakMessage = when {
                        gamificationStats.streak <= 1 -> "¿Listo para empezar el reto? \uD83D\uDCAA"
                        gamificationStats.streak <= 4 -> "¡Excelente inicio! Sigue así \uD83C\uDF31"
                        gamificationStats.streak <= 10 -> "¡Sigue esforzándote! \uD83D\uDE80"
                        gamificationStats.streak <= 30 -> "¡Imparable! Tienes un autocontrol de hierro \uD83D\uDD25"
                        else -> "¡Maestro Zen! Eres una inspiración \uD83E\uDDD8"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\u2B50", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(streakMessage, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
