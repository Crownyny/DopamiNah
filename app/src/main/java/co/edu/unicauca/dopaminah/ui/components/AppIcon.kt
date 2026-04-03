package co.edu.unicauca.dopaminah.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.R

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    Image(
        painter = painterResource(id = R.drawable.full_icon),
        contentDescription = "App Icon",
        modifier = modifier.size(size.dp)
    )
}
