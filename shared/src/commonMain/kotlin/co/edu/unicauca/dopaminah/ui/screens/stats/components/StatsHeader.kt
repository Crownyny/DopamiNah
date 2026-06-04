package co.edu.unicauca.dopaminah.ui.screens.stats.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsHeader() {
    Text(
        "Estadísticas",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(horizontal = 24.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Análisis detallado de tu uso",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 24.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )
}
