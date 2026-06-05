package co.edu.unicauca.dopaminah.ui.screens.goals.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.icons.LucideLock
import co.edu.unicauca.dopaminah.ui.icons.LucidePencil
import co.edu.unicauca.dopaminah.ui.icons.LucideSmartphone
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer
import co.edu.unicauca.dopaminah.ui.icons.LucideTrash
import co.edu.unicauca.dopaminah.ui.icons.LucideTriangleAlert
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalDisplayModel
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalType
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun GoalCard(
    goal: GoalDisplayModel,
    onDelete: () -> Unit = {},
    onEdit: (newLimitMinutes: Int) -> Unit = {}
) {
    var isBypassed by remember(goal.appPackageName) {
        mutableStateOf(goal.appPackageName?.let { co.edu.unicauca.dopaminah.isAppBypassed(it) } ?: false)
    }
    val icon = when (goal.goalType) {
        GoalType.TOTAL_DAILY -> LucideTimer
        GoalType.APP_LIMIT -> LucideSmartphone
        GoalType.UNLOCK_LIMIT -> LucideLock
        else -> LucideTimer
    }
    val iconBgColor = if (goal.isExceeded)
        MaterialTheme.extendedColors.dangerRed.copy(alpha = 0.1f)
    else
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    val iconTintColor = if (goal.isExceeded)
        MaterialTheme.extendedColors.dangerRed
    else
        MaterialTheme.colorScheme.primary

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar meta",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Seguro que quieres eliminar \"${goal.title}\"? Esta accion no se puede deshacer.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text(
                        "Eliminar",
                        color = MaterialTheme.extendedColors.dangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showEditDialog) {
        EditGoalDialog(
            currentLimitMinutes = goal.currentLimitMinutes,
            goalTitle = goal.title,
            isUnlockType = goal.goalType == GoalType.UNLOCK_LIMIT,
            onDismiss = { showEditDialog = false },
            onSave = { newLimit ->
                showEditDialog = false
                onEdit(newLimit)
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = goal.subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = LucidePencil,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = LucideTrash,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.extendedColors.dangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (goal.isExceeded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = LucideTriangleAlert,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.dangerRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Limite excedido",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.extendedColors.dangerRed
                    )
                }
            }

            if (isBypassed && goal.appPackageName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = LucideTriangleAlert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bloqueo omitido temporalmente",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = {
                            co.edu.unicauca.dopaminah.removeBypassApp(goal.appPackageName)
                            isBypassed = false
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Re-bloquear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.progressLabel,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = goal.progressPercent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (goal.isExceeded) MaterialTheme.extendedColors.dangerRed
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { goal.progressFraction },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (goal.isExceeded) MaterialTheme.extendedColors.dangerRed
                else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}


