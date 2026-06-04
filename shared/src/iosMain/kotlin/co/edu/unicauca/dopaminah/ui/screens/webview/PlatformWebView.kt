package co.edu.unicauca.dopaminah.ui.screens.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

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
                val nsUrl = NSURL.URLWithString(url)
                if (nsUrl != null) {
                    UIApplication.sharedApplication.openURL(nsUrl)
                }
            }
        ) {
            Text(
                text = "Abrir en Safari",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
