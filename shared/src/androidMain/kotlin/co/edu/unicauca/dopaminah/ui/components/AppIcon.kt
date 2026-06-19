package co.edu.unicauca.dopaminah.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
actual fun AppIcon(size: Dp, modifier: Modifier) {
    val painter = painterResource(co.edu.unicauca.dopaminah.shared.R.drawable.full_icon)
    Image(
        painter = painter,
        contentDescription = "App Icon",
        modifier = modifier.size(size)
    )
}
