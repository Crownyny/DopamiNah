# Guía de desarrollo: PWA — DopamiNah Web como navegador enfocado

## Visión general

Convertir la webApp en una **PWA instalable** con un navegador enfocado (focus browser) que:

- Bloquee sitios distractores durante sesiones de enfoque
- Lleve registro de tiempo por dominio visitado
- Funcione offline con datos locales
- Sea instalable en la pantalla de inicio

---

## Fase 1 — Infraestructura PWA básica

### 1.1. Agregar meta tags y manifest al HTML

**Archivo:** `webApp/src/webMain/resources/index.html`

Agregar al `<head>`:

```html
<!-- PWA meta tags -->
<meta name="theme-color" content="#6D28D9">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
<meta name="apple-mobile-web-app-title" content="DopamiNah">
<link rel="manifest" href="manifest.json">
<link rel="icon" type="image/svg+xml" href="favicon.svg">
<link rel="apple-touch-icon" href="icon-192.png">
```

Eliminar el `<script>` directo a `webApp.js` — el plugin de Kotlin lo inyecta automáticamente.

### 1.2. Crear manifest.json

**Archivo:** `webApp/src/webMain/resources/manifest.json`

```json
{
  "name": "DopamiNah",
  "short_name": "DopamiNah",
  "description": "Navegador enfocado contra la procrastinación digital",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#1C1B1F",
  "theme_color": "#6D28D9",
  "icons": [
    { "src": "icon-192.png", "sizes": "192x192", "type": "image/png" },
    { "src": "icon-512.png", "sizes": "512x512", "type": "image/png" },
    { "src": "icon-512.png", "sizes": "512x512", "type": "image/png", "purpose": "maskable" }
  ]
}
```

### 1.3. Crear service worker

**Archivo:** `webApp/src/webMain/resources/sw.js`

```javascript
const CACHE = 'dopaminah-v1';
const ASSETS = [
  '/',
  '/index.html',
  '/webApp.js',
  '/styles.css',
  '/favicon.svg',
  '/icon-192.png',
  '/icon-512.png'
];

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(cache => cache.addAll(ASSETS))
  );
});

self.addEventListener('fetch', e => {
  e.respondWith(
    caches.match(e.request).then(cached => {
      const fetched = fetch(e.request).then(response => {
        caches.open(CACHE).then(cache => cache.put(e.request, response.clone()));
        return response;
      });
      return cached || fetched;
    })
  );
});

self.addEventListener('activate', e => {
  e.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)))
    )
  );
});
```

### 1.4. Agregar assets visuales

Crear en `webApp/src/webMain/resources/`:
- `favicon.svg` — logo DopamiNah en SVG monocromático
- `icon-192.png` — icono 192×192 PNG
- `icon-512.png` — icono 512×512 PNG

Usar el mismo brain icon de `shared/src/androidMain/res/drawable/full_icon.xml` convertido a PNG/SVG.

### 1.5. Registrar el service worker

**Archivo:** `webApp/src/webMain/kotlin/co/edu/unicauca/dopaminah/main.kt`

Agregar al inicio de `main()`:

```kotlin
import kotlinx.browser.window
import org.w3c.dom.events.Event

fun registerServiceWorker() {
    if ("serviceWorker" in window.navigator) {
        window.navigator.serviceWorker.register("/sw.js")
    }
}
```

Llamarlo antes de `ComposeViewport`.

---

## Fase 2 — Navegador enfocado (Focus Browser)

### 2.1. Modelo de datos para sitios web

**Crear:** `shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/domain/model/WebSite.kt`

```kotlin
data class WebSite(
    val domain: String,
    val title: String = "",
    val timeSpentSeconds: Long = 0,
    val lastVisited: Long = 0,
    val visitCount: Int = 0
)

data class BlockRule(
    val domain: String,
    val isBlocked: Boolean = true
)
```

### 2.2. Repositorio de navegación web

**Crear:** `shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/data/repository/WebNavigationRepository.kt`

```kotlin
class WebNavigationRepository {
    private val _sites = MutableStateFlow<List<WebSite>>(emptyList())
    val sites: StateFlow<List<WebSite>> = _sites.asStateFlow()

    private val _blockRules = MutableStateFlow<List<BlockRule>>(emptyList())
    val blockRules: StateFlow<List<BlockRule>> = _blockRules.asStateFlow()

    private val activeTimers = mutableMapOf<String, Long>() // domain -> start time

    fun trackNavigationStart(domain: String) {
        activeTimers[domain] = currentTimeMillis()
    }

    fun trackNavigationEnd(domain: String, pageTitle: String = "") {
        val start = activeTimers.remove(domain) ?: return
        val elapsed = (currentTimeMillis() - start) / 1000
        _sites.value = _sites.value.map { site ->
            if (site.domain == domain) {
                site.copy(
                    title = pageTitle.ifEmpty { site.title },
                    timeSpentSeconds = site.timeSpentSeconds + elapsed,
                    lastVisited = currentTimeMillis(),
                    visitCount = site.visitCount + 1
                )
            } else site
        } + if (_sites.value.none { it.domain == domain }) {
            listOf(WebSite(domain = domain, timeSpentSeconds = elapsed, lastVisited = currentTimeMillis(), visitCount = 1))
        } else emptyList()
    }

    fun addBlockRule(domain: String) {
        _blockRules.value = _blockRules.value + BlockRule(domain)
    }

    fun removeBlockRule(domain: String) {
        _blockRules.value = _blockRules.value.filter { it.domain != domain }
    }

    fun isBlocked(domain: String): Boolean {
        return _blockRules.value.any { it.domain == domain && it.isBlocked }
    }
}
```

### 2.3. Componente PlatformWebView con tracking

**Modificar** `shared/src/commonMain/.../PlatformWebView.kt` para aceptar callbacks de navegación:

```kotlin
@Composable
expect fun PlatformWebView(
    url: String,
    state: WebViewState,
    modifier: Modifier = Modifier,
    onNavigationChanged: (domain: String, title: String, isStart: Boolean) -> Unit = { _, _, _ -> }
)
```

**Android actual** — modificar para extraer el dominio del URL y llamar `onNavigationChanged`:

```kotlin
// Dentro de WebViewClient:
override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
    url?.let { onNavigationChanged(extractDomain(it), "", true) }
}

override fun onPageFinished(view: WebView?, url: String?) {
    url?.let {
        state.pageTitle.let { title ->
            onNavigationChanged(extractDomain(it), title, false)
        }
    }
}
```

### 2.4. Screen WebViewScreen con bloqueo

**Modificar** `WebViewScreen.kt`:

- Antes de cargar una URL, verificar `repository.isBlocked(domain)`
- Si está bloqueado, mostrar pantalla de bloqueo en vez del WebView
- Botón "Desbloquear por hoy" o "Ir de todas formas"

```kotlin
if (repository.isBlocked(extractDomain(url))) {
    BlockedSiteScreen(domain = extractDomain(url), onProceed = { /* override */ })
} else {
    PlatformWebView(url, state, onNavigationChanged = { domain, title, isStart ->
        if (isStart) repository.trackNavigationStart(domain)
        else repository.trackNavigationEnd(domain, title)
    })
}
```

### 2.5. Variables adicionales útiles

**Crear:** `shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/util/UrlUtils.kt`

```kotlin
fun extractDomain(url: String): String {
    return url.removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
        .substringBefore("/")
        .substringBefore(":")
}
```

---

## Fase 3 — Dashboard de navegación web

### 3.1. Nueva screen en web: WebStatsScreen

Mostrar en la web (reemplazando StatsScreen que no tiene sentido):

- **Hoy:** tiempo total de navegación por dominio
- **Top sitios:** ranking por tiempo/visitas
- **Sesión actual:** cuánto llevas navegando y en qué sitios
- **Sitios bloqueados:** lista de reglas activas

### 3.2. Timer de enfoque

Añadir un timer Pomodoro/Focus-mode:

- Inicia una sesión de enfoque (25 min)
- Durante la sesión, todos los sitios en la blacklist se bloquean
- Al terminar, muestra resumen de la sesión
- Persiste en localStorage via `DevicePreferences`

---

## Fase 4 — Persistencia offline

### 4.1. localStorage con multiplatform-settings

El proyecto ya usa `multiplatform-settings`. Usar `DevicePreferences` (ya implementado para JS/Wasm) para persistir:

```kotlin
class WebSettingsRepository(prefs: DevicePreferences) {
    var savedSites: List<WebSite>
        get() = prefs.getString("saved_sites", "[]").let { parseJson(it) }
        set(value) = prefs.putString("saved_sites", toJson(value))

    var blockRules: List<BlockRule>
        get() = prefs.getString("block_rules", "[]").let { parseJson(it) }
        set(value) = prefs.putString("block_rules", toJson(value))
}
```

### 4.2. Servicio de caché en SW

El service worker de la Fase 1 ya cachea los assets. Extenderlo en Fase 4 para cachear datos:

```javascript
// En sw.js, agregar ruta API de datos
self.addEventListener('fetch', e => {
  if (e.request.url.includes('/api/')) {
    e.respondWith(networkFirst(e.request));
    return;
  }
  e.respondWith(cacheFirst(e.request));
});
```

---

## Fase 5 — Build y deployment

### 5.1. Producción

```bash
./gradlew :webApp:jsBrowserDistribution    # JS prod build
./gradlew :webApp:wasmJsBrowserDistribution # Wasm prod build
```

Output en:
- `webApp/build/kotlin-webpack/js/productionExecutable/`
- `webApp/build/dist/wasmJs/productionExecutable/`

### 5.2. Deploy

Los directorios de salida contienen: `index.html`, `webApp.js`, `styles.css`, `favicon.svg`, `manifest.json`, `sw.js`, `icon-*.png`, más `skiko.wasm` y `skiko.mjs`.

Desplegar todo a cualquier static host (Netlify, Vercel, Firebase Hosting, GitHub Pages).

### 5.3. HTTPS obligatorio

Los service workers solo funcionan en HTTPS (o localhost). Asegurar HTTPS en producción.

---

## Resumen de archivos a crear/modificar

| Archivo | Acción |
|---------|--------|
| `webApp/src/webMain/resources/index.html` | Modificar: meta tags, manifest link, favicon |
| `webApp/src/webMain/resources/manifest.json` | Crear |
| `webApp/src/webMain/resources/sw.js` | Crear |
| `webApp/src/webMain/resources/favicon.svg` | Crear (convertir brain icon) |
| `webApp/src/webMain/resources/icon-192.png` | Crear |
| `webApp/src/webMain/resources/icon-512.png` | Crear |
| `webApp/src/webMain/kotlin/.../main.kt` | Modificar: registrar SW |
| `shared/.../domain/model/WebSite.kt` | Crear |
| `shared/.../domain/model/BlockRule.kt` | Crear (o en mismo archivo) |
| `shared/.../data/repository/WebNavigationRepository.kt` | Crear |
| `shared/.../util/UrlUtils.kt` | Crear |
| `shared/.../ui/screens/webview/PlatformWebView.kt` | Modificar: agregar `onNavigationChanged` callback |
| `shared/.../ui/screens/webview/WebViewScreen.kt` | Modificar: integración con repositorio + bloqueo |
| `shared/.../ui/screens/web/BlockedSiteScreen.kt` | Crear |
| `shared/.../ui/screens/web/WebStatsScreen.kt` | Crear |
| `shared/.../data/repository/WebSettingsRepository.kt` | Crear (opcional, Fase 4) |
