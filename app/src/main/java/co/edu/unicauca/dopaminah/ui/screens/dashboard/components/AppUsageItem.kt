package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppUsageItem(
    usageSummary: AppUsageSummary,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    
    val appName = usageSummary.appName
    val appInfo = try {
        packageManager.getApplicationInfo(usageSummary.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    val appIcon = appInfo?.loadIcon(packageManager)
    
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Icon
            if (appIcon != null) {
                Image(
                    painter = rememberDrawablePainter(drawable = appIcon),
                    contentDescription = "$appName Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surface)
                        .padding(4.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App Name & Usage
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                )
                Text(
                    text = UsageTimeUtils.formatUsageTime(usageSummary.totalTimeForegroundMillis),
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
