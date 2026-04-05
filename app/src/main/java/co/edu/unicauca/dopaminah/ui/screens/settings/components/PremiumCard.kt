package co.edu.unicauca.dopaminah.ui.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun PremiumCard(onClick: () -> Unit) {
    val extended = MaterialTheme.extendedColors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = extended.brandOrange),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Premium", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text("Desbloquea todas las funciones", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$9.99", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Pago único", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }

            Spacer(Modifier.height(16.dp))

            val perks = listOf(
                "✨" to "Estadísticas avanzadas por hora",
                "🎯" to "Metas personalizadas ilimitadas",
                "📊" to "Análisis de comportamiento detallado",
                "📤" to "Exportación de datos",
                "🚫" to "Sin publicidad"
            )

            perks.forEach { (emoji, text) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(emoji, modifier = Modifier.padding(end = 8.dp))
                    Text(text, fontSize = 14.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = extended.brandOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text("Iniciar sesión para desbloquear", fontWeight = FontWeight.Bold)
            }
        }
    }
}
