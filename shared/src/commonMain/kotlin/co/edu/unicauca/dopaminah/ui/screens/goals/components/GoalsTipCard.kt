package co.edu.unicauca.dopaminah.ui.screens.goals.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun GoalsTipCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.extendedColors.aboutSurface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "💡 Consejo del Día",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Empieza con metas pequeñas. Reducir 15 minutos al día puede marcar una gran diferencia a largo plazo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
