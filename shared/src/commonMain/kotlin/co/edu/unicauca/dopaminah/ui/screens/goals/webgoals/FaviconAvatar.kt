package co.edu.unicauca.dopaminah.ui.screens.goals.webgoals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

@Composable
fun FaviconAvatar(
    domain: String,
    size: Dp = 44.dp,
    fontSize: TextUnit = 18.sp
) {
    var showFallback by remember { mutableStateOf(true) }
    val clean = cleanDomain(domain)
    val color = brandColor(domain)

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.13f)),
        contentAlignment = Alignment.Center
    ) {
        if (showFallback) {
            DomainFallbackIcon(clean, color, size, fontSize)
        }

        AsyncImage(
            model = "https://www.google.com/s2/favicons?domain=$domain&sz=64",
            contentDescription = "$domain favicon",
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(12.dp)),
            onSuccess = { showFallback = false },
            onError = { showFallback = true }
        )
    }
}
