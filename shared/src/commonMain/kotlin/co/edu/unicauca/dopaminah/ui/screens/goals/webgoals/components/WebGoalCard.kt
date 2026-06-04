package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.icons.LucidePencil
import co.edu.unicauca.dopaminah.ui.icons.LucideShieldAlert
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer
import co.edu.unicauca.dopaminah.ui.icons.LucideTrash
import co.edu.unicauca.dopaminah.ui.screens.goals.webgoals.WebGoalUiModel
import co.edu.unicauca.dopaminah.ui.theme.DangerRed
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import co.edu.unicauca.dopaminah.ui.theme.SuccessGreen
import co.edu.unicauca.dopaminah.ui.theme.WarningYellow

private val domainColorPalette = listOf(
    Color(0xFF8B5CF6),
    Color(0xFF3B82F6),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFFEC4899),
    Color(0xFF6366F1),
    Color(0xFF14B8A6),
    Color(0xFFF97316),
    Color(0xFF06B6D4),
    Color(0xFF84CC16),
    Color(0xFFD946EF),
)

private fun domainColor(domain: String): Color {
    val clean = domain.removePrefix("www.").lowercase()
    val index = (clean.hashCode() and Int.MAX_VALUE) % domainColorPalette.size
    return domainColorPalette[index]
}

private fun domainInitial(domain: String): String {
    return domain.removePrefix("www.").first().uppercase()
}

@Composable
fun WebGoalCard(
    goal: WebGoalUiModel,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onToggle: () -> Unit = {}
) {
    val isDanger = goal.progressFraction > 0.85f && !goal.isBlocked
    val isWarning = goal.progressFraction in 0.6f..0.85f && !goal.isBlocked

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                goal.isBlocked -> DangerRed.copy(alpha = 0.04f)
                !goal.isActive -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DomainAvatar(
                    domain = goal.domain,
                    isBlocked = goal.isBlocked,
                    size = 44.dp,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.domain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            goal.isBlocked -> Color.Black.copy(alpha = 0.85f)
                            !goal.isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 0.2.sp
                    )
                    Text(
                        text = goal.displayUrl,
                        fontSize = 12.sp,
                        color = when {
                            goal.isBlocked -> DangerRed.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (goal.isBlocked) {
                    Text(
                        text = "Bloqueado",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        letterSpacing = 0.3.sp
                    )
                } else if (goal.isActive) {
                    Text(
                        text = "Activo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessGreen,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = LucideTimer,
                        contentDescription = null,
                        tint = when {
                            goal.isBlocked -> DangerRed
                            isDanger -> WarningYellow
                            else -> DopaminahPurple
                        },
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${goal.todaySpentMinutes} min",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            goal.isBlocked -> DangerRed
                            isDanger -> WarningYellow
                            !goal.isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Text(
                    text = "de ${goal.dailyTimeLimitMinutes} min",
                    fontSize = 13.sp,
                    color = if (!goal.isActive)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            GradientProgressBar(
                fraction = goal.progressFraction,
                isBlocked = goal.isBlocked,
                isWarning = isWarning,
                isDanger = isDanger
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        goal.isBlocked -> "Limite alcanzado — bloqueado hasta mañana"
                        goal.remainingMinutes <= 5 -> "Quedan solo ${goal.remainingMinutes} min"
                        else -> "${goal.remainingMinutes} min restantes"
                    },
                    fontSize = 12.sp,
                    color = when {
                        goal.isBlocked -> DangerRed
                        isDanger -> WarningYellow
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (goal.isBlocked || isDanger) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    if (!goal.isBlocked) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = LucidePencil,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = LucideTrash,
                            contentDescription = "Eliminar",
                            tint = if (goal.isBlocked) DangerRed.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DomainAvatar(
    domain: String,
    isBlocked: Boolean,
    size: Dp = 44.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp
) {
    val bg = if (isBlocked) DangerRed.copy(alpha = 0.1f) else domainColor(domain).copy(alpha = 0.13f)
    val fg = if (isBlocked) DangerRed else domainColor(domain)
    val icon = if (isBlocked) LucideShieldAlert else null
    val letter = if (!isBlocked) domainInitial(domain) else null

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(size * 0.48f)
            )
        } else if (letter != null) {
            Text(
                text = letter,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = fg,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GradientProgressBar(
    fraction: Float,
    isBlocked: Boolean,
    isWarning: Boolean,
    isDanger: Boolean
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val gradientColors = when {
        isBlocked -> listOf(DangerRed, DangerRed.copy(alpha = 0.6f))
        isDanger -> listOf(WarningYellow, WarningYellow.copy(alpha = pulseAlpha))
        isWarning -> listOf(DopaminahOrange, DopaminahPurple)
        else -> listOf(DopaminahPurple, DopaminahPurpleDark)
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = Modifier.fillMaxWidth().height(10.dp)
    ) {
        val barWidth = size.width * fraction.coerceIn(0f, 1f)
        val r = 5.dp.toPx()

        drawRoundRect(color = trackColor, size = size, cornerRadius = CornerRadius(r))
        if (barWidth > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(gradientColors),
                size = size.copy(width = barWidth),
                cornerRadius = CornerRadius(r)
            )
        }
    }
}
