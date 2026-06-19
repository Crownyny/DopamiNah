package co.edu.unicauca.dopaminah.ui.screens.achievements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.icons.LucideLock
import co.edu.unicauca.dopaminah.ui.icons.LucideSunrise
import co.edu.unicauca.dopaminah.ui.icons.LucideCalendar
import co.edu.unicauca.dopaminah.ui.icons.LucideZap
import co.edu.unicauca.dopaminah.ui.icons.LucideAward
import co.edu.unicauca.dopaminah.ui.icons.LucideTarget
import co.edu.unicauca.dopaminah.ui.icons.LucideTrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.BadgeUi
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark

private fun getBadgeIcon(badgeId: String): ImageVector {
    return when (badgeId) {
        "primer_paso" -> LucideSunrise
        "racha_3" -> LucideCalendar
        "racha_7" -> LucideZap
        "racha_14" -> LucideAward
        "racha_30" -> LucideAward
        "focus" -> LucideTarget
        "reduction" -> LucideTrendingUp
        "autodisciplina" -> LucideLock
        else -> LucideAward
    }
}

@Composable
fun BadgesGrid(badges: List<BadgeUi>) {
    val rows = badges.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowBadges ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowBadges.forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
                if (rowBadges.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: BadgeUi, modifier: Modifier = Modifier) {
    if (badge.isUnlocked) {
        Box(
            modifier = modifier
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DopaminahPurple, DopaminahPurpleDark)
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = getBadgeIcon(badge.id),
                    contentDescription = badge.title,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = badge.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = badge.description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = LucideLock,
                contentDescription = "Insignia bloqueada",
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
