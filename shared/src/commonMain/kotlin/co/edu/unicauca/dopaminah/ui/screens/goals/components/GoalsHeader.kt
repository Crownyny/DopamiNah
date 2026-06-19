package co.edu.unicauca.dopaminah.ui.screens.goals.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange

/** Header tip card for the device-usage goals screen. */
@Composable
fun GoalsHeader(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DopaminahOrange),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "¡Alcanza tu libertad digital!",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Define límites inteligentes y recupera tu tiempo para lo que realmente importa.",
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}
