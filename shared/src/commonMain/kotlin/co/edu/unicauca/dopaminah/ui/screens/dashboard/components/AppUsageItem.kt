package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils

@Composable
fun AppUsageItem(
    usageSummary: AppUsageSummary,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AppIconImage(
                iconBytes = usageSummary.iconBytes,
                appName = usageSummary.appName,
                size = 50.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usageSummary.appName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface, maxLines = 1)
                Text(
                    UsageTimeUtils.formatUsageTime(usageSummary.totalTimeForegroundMillis),
                    fontSize = 14.sp, color = colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
