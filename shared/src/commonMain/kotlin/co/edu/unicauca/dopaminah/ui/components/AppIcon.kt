package co.edu.unicauca.dopaminah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
expect fun AppIcon(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
)
