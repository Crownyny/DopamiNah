package co.edu.unicauca.dopaminah.ui.screens.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.icons.LucideChevronLeft
import co.edu.unicauca.dopaminah.ui.icons.LucideChevronRight
import co.edu.unicauca.dopaminah.ui.icons.LucideX
import co.edu.unicauca.dopaminah.ui.screens.focusbrowser.BlockedSiteScreen
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
/** In-app browser screen with navigation controls and domain-blocking support. */
@Composable
fun WebViewScreen(
    url: String,
    onClose: () -> Unit,
    shouldCheckBlock: ((String) -> Boolean)? = null
) {
    val state = remember { WebViewState() }
    state.shouldCheckBlock = shouldCheckBlock

    LaunchedEffect(url, shouldCheckBlock) {
        val check = shouldCheckBlock ?: return@LaunchedEffect
        if (check(url)) {
            state.isBlocked = true
            state.blockedUrl = url
        }
    }

    if (state.isBlocked) {
        BlockedSiteScreen(
            url = state.blockedUrl,
            onGoBack = {
                state.isBlocked = false
                state.blockedUrl = ""
                state.onGoBack()
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DopaminahPurpleDark)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(LucideX, contentDescription = "Cerrar", tint = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.pageTitle.ifEmpty { "Cargando..." },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(
                        onClick = { state.onGoBack() },
                        enabled = state.canGoBack
                    ) {
                        Icon(LucideChevronLeft, contentDescription = "Atr\u00e1s", tint = Color.White)
                    }
                    IconButton(
                        onClick = { state.onGoForward() },
                        enabled = state.canGoForward
                    ) {
                        Icon(LucideChevronRight, contentDescription = "Adelante", tint = Color.White)
                    }
                }
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            PlatformWebView(
                url = url,
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
