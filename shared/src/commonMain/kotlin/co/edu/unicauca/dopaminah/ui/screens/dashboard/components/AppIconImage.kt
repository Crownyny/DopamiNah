package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.utils.decodeToImageBitmap

@Composable
fun AppIconImage(
    iconBytes: ByteArray?,
    appName: String,
    size: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(iconBytes) {
        if (iconBytes != null) iconBytes.decodeToImageBitmap() else null
    }

    if (imageBitmap != null) {
        androidx.compose.foundation.Image(
            painter = BitmapPainter(imageBitmap),
            contentDescription = appName,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = appName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = (size.value * 0.4).sp
            )
        }
    }
}
