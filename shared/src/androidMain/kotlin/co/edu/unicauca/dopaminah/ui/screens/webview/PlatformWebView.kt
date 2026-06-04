package co.edu.unicauca.dopaminah.ui.screens.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PlatformWebView(
    url: String,
    state: WebViewState,
    modifier: Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    state.isLoading = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    state.isLoading = false
                    state.canGoBack = view?.canGoBack() ?: false
                    state.canGoForward = view?.canGoForward() ?: false
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    state.pageTitle = title ?: ""
                }
            }
        }
    }

    DisposableEffect(Unit) {
        state.onGoBack = { webView.goBack() }
        state.onGoForward = { webView.goForward() }
        state.onReload = { webView.reload() }
        webView.loadUrl(url)
        onDispose {
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}
