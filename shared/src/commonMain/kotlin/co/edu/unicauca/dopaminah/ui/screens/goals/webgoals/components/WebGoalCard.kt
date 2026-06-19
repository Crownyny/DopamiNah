package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.icons.LucidePencil
import co.edu.unicauca.dopaminah.ui.icons.LucideTrash
import co.edu.unicauca.dopaminah.ui.icons.LucideZap
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.BrandAvatar
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalUiModel
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.brandColor
import co.edu.unicauca.dopaminah.ui.theme.DangerRed
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.SuccessGreen
import co.edu.unicauca.dopaminah.ui.theme.WarningYellow

/** Minimalist card for a web goal, with a 3dp brand-colored accent bar, domain avatar, progress bar, and action buttons. */
@Composable
fun WebGoalCard(
    goal: WebGoalUiModel,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onToggle: () -> Unit = {}
) {
    val accentColor = brandColor(goal.domain)
    val isBlocked = goal.isBlocked
    val isImmediate = goal.dailyTimeLimitMinutes == 0
    val isDanger = goal.progressFraction > 0.85f && !isBlocked && !isImmediate

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBlocked) DangerRed.copy(alpha = 0.03f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        when {
                            isBlocked -> DangerRed
                            isDanger -> WarningYellow
                            isImmediate -> DangerRed
                            else -> accentColor
                        }
                    )
            )

            if (isImmediate) {
                ImmediateContent(goal = goal, onEdit = onEdit, onDelete = onDelete)
            } else {
                TimedContent(
                    goal = goal,
                    accentColor = if (isBlocked) DangerRed else if (isDanger) WarningYellow else accentColor,
                    isBlocked = isBlocked,
                    isDanger = isDanger,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun ImmediateContent(
    goal: WebGoalUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandAvatar(
                domain = goal.domain,
                isBlocked = false,
                size = 28.dp,
                fontSize = 12.sp
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.domain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = goal.displayUrl,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = LucidePencil,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(13.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = LucideTrash,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DangerRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LucideZap,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = "Sin límite",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DangerRed
                )
                Text(
                    text = "Se bloquea al visitar el sitio",
                    fontSize = 11.sp,
                    color = DangerRed.copy(alpha = 0.55f)
                )
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun TimedContent(
    goal: WebGoalUiModel,
    accentColor: Color,
    isBlocked: Boolean,
    isDanger: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandAvatar(
                domain = goal.domain,
                isBlocked = isBlocked,
                size = 28.dp,
                fontSize = 12.sp
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.domain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isBlocked -> DangerRed
                        !goal.isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = goal.displayUrl,
                    fontSize = 10.sp,
                    color = if (isBlocked) DangerRed.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = LucidePencil,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(13.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = LucideTrash,
                        contentDescription = "Eliminar",
                        tint = if (isBlocked) DangerRed.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            val timeColor = when {
                isBlocked -> DangerRed
                isDanger -> WarningYellow
                !goal.isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                else -> MaterialTheme.colorScheme.onSurface
            }

            Text(
                text = formatTimeShort(goal.todaySpentMinutes),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = timeColor,
                letterSpacing = -0.5.sp
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "/ ${formatTimeShort(goal.dailyTimeLimitMinutes)}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = goal.progressFraction.coerceIn(0f, 1f))
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        if (goal.isActive) accentColor
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dotColor = when {
                isBlocked -> DangerRed
                isDanger -> WarningYellow
                goal.remainingMinutes <= 10 -> DopaminahOrange
                goal.remainingMinutes <= 30 -> WarningYellow
                else -> SuccessGreen
            }
            val statusText = when {
                isBlocked -> "Agotado"
                isDanger -> "Al límite"
                goal.remainingMinutes <= 3 -> "¡${goal.remainingMinutes} min!"
                else -> "${goal.remainingMinutes} min"
            }

            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(dotColor)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = statusText,
                fontSize = 11.sp,
                color = when {
                    isBlocked -> DangerRed
                    isDanger -> WarningYellow
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "${(goal.progressFraction * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    isBlocked -> DangerRed.copy(alpha = 0.5f)
                    !goal.isActive -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    else -> accentColor.copy(alpha = 0.7f)
                }
            )
        }
    }
}

private fun formatTimeShort(minutes: Int): String {
    if (minutes < 60) return "${minutes}m"
    val h = minutes / 60
    val r = minutes % 60
    return if (r > 0) "${h}h ${r}m" else "${h}h"
}
