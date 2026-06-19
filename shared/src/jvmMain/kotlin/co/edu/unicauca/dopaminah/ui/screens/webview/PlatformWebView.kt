package co.edu.unicauca.dopaminah.ui.screens.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.awt.Desktop
import java.net.URI

@Composable
actual fun PlatformWebView(
    url: String,
    state: WebViewState,
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                try {
                    Desktop.getDesktop().browse(URI(url))
                } catch (_: Exception) { }
            }
        ) {
            Text(
                text = "Abrir en navegador",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
