package co.edu.unicauca.dopaminah.ui.screens.achievements.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.domain.utils.BadgeDefinitions
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun StreakCard(streakDays: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.extendedColors.brandOrange),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "RACHA ACTUAL",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$streakDays",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    " días consecutivos",
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                BadgeDefinitions.getStreakMotivation(streakDays),
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}
