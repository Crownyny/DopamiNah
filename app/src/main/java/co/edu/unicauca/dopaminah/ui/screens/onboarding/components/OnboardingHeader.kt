package co.edu.unicauca.dopaminah.ui.screens.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark

@Composable
fun OnboardingHeader(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPages: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Configuración",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DopaminahPurpleDark
        )
        
        // Indicators
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(totalPages) { index ->
                Box(
                    modifier = Modifier
                        .width(if (currentPage == index) 24.dp else 24.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (currentPage == index) DopaminahPurple 
                            else Color.LightGray.copy(alpha=0.5f)
                        )
                )
            }
        }
    }
}
