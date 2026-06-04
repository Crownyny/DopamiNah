package co.edu.unicauca.dopaminah.ui.screens.webview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class WebViewState {
    var pageTitle by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    var isBlocked by mutableStateOf(false)
    var blockedUrl by mutableStateOf("")
    var onGoBack: () -> Unit = {}
    var onGoForward: () -> Unit = {}
    var onReload: () -> Unit = {}
    var shouldCheckBlock: ((String) -> Boolean)? = null
}

@Composable
expect fun PlatformWebView(
    url: String,
    state: WebViewState,
    modifier: Modifier = Modifier
)
