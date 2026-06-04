package co.edu.unicauca.dopaminah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange

@Composable
actual fun AppIcon(size: Dp, modifier: Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 4))
            .background(DopaminahOrange),
        contentAlignment = Alignment.Center
    ) {
        BrainIcon(
            modifier = Modifier.size(size * 0.7f)
        )
    }
}
