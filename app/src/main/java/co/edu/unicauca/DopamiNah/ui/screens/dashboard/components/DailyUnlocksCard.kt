package co.edu.unicauca.DopamiNah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.DopamiNah.R
import co.edu.unicauca.DopamiNah.ui.theme.*

@Composable
fun DailyUnlocksCard(dailyUnlocks: Int, yesterdayUnlocks: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DopaminahRedDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.dashboard_unlocks),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = DopaminahRedText
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$dailyUnlocks",
                    color = DopaminahRedText,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 64.sp
                )
                Text(
                    stringResource(R.string.dashboard_unlocks_times),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val diff = dailyUnlocks - yesterdayUnlocks
            val diffText = when {
                diff > 0 -> "↗ +$diff vs ayer"
                diff < 0 -> "↘ $diff vs ayer"
                else -> "= Igual que ayer"
            }
            
            val diffColor = if (diff <= 0) SuccessGreen else DopaminahRedText

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    diffText,
                    color = diffColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
