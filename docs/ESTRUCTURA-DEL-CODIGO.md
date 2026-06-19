# DopamiNah — Estructura del Código

## Visión General

DopamiNah es una aplicación **Kotlin Multiplatform (KMP) con Compose Multiplatform** que monitoriza el uso del dispositivo y ayuda a gestionar metas de tiempo frente a pantallas. Soporta **Android, Web (JS/Wasm), iOS, Desktop (JVM)** y una **extensión de navegador Chrome/Edge**.

---

## Arquitectura General (Clean Architecture / Capas)

```
┌────────────────────────────────────────────────────────────────┐
│                    shared/ (commonMain)                         │
│  ┌──────────┐  ┌────────────┐  ┌──────────────┐  ┌──────────┐ │
│  │  domain  │  │   data     │  │     ui       │  │  utils   │ │
│  │ (modelos,│  │ (repo impl,│  │ (screens,    │  │ (const,  │ │
│  │  repos,  │  │  DB SQLite)│  │  viewmodels, │  │  dates,  │ │
│  │  usecases)│  │            │  │  navigation, │  │  time)   │ │
│  └──────────┘  └────────────┘  │  components)  │  └──────────┘ │
│                                └──────────────┘               │
└────────────────────────────────────────────────────────────────┘
         │              │               │              │
    androidApp      webApp        iosApp/desktop     browser-extension
   (Android APK)  (Web App)      (native/desktop)   (Chrome/Edge ext)
```

---

## 1. Módulo `shared/` — Código Compartido (Kotlin Multiplatform)

### 1.1 Capa `domain/` (Modelos + Contratos — PURA, 100% implementada)

Contiene los modelos de dominio, interfaces de repositorio y casos de uso. **Todo está completamente implementado — no hay placeholders.**

#### `domain/model/` — Modelos de datos

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `AppLimitGoal.kt` | **Completo** | Meta de límite por app: packageName, maxTimeMillis, maxUnlocks, streak |
| `AppUsageSummary.kt` | **Completo** | Resumen de uso de app individual: foreground time, unlocks, icon bytes |
| `GoalType.kt` | **Completo** | Tipos de meta (APP_LIMIT, TOTAL_DAILY, UNLOCK_LIMIT) |
| `UserGamificationStats.kt` | **Completo** | Estadísticas de gamificación: nivel, puntos, racha, badges |
| `UserPremiumStatus.kt` | **Completo** | Estado premium del usuario |

```kotlin
// Ejemplo: AppLimitGoal.kt
data class AppLimitGoal(
    val id: Int = 0,
    val goalType: String,
    val packageName: String = "",
    val appDisplayName: String = "",
    val maxTimeMillis: Long = 0L,
    val maxUnlocks: Int = 0,
    val currentStreak: Int = 0
)
```

#### `domain/repository/` — Interfaces de repositorio

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `DeviceUsageRepository.kt` | **Completo** | Consultar estadísticas de uso del dispositivo |
| `GoalsRepository.kt` | **Completo** | CRUD de metas de apps (Flow) |
| `GamificationRepository.kt` | **Completo** | Leer/actualizar gamificación (nivel, puntos, rachas) |
| `WebGoalsRepository.kt` | **Completo** | Persistir metas web y tiempo acumulado por dominio |
| `UsageMonitoringRepository.kt` | **Completo** | Monitorización en tiempo real: screen time, unlocks, alertas |
| `PremiumRepository.kt` | **Completo** | Status premium del usuario |
| `AuthRepository.kt` | **Completo** | Autenticación (Google Sign-In) |
| `DataTypes.kt` | **Completo** | Data classes auxiliares: `DailyDetailStats`, `MonitoringStats` |

#### `domain/usecase/` — Casos de uso

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `GetDashboardDataUseCase.kt` | **Completo** | Combina metas + uso en vivo para tarjetas de límite |
| `CheckUsageLimitsUseCase.kt` | **Completo** | Verifica límites y dispara notificaciones |
| `GetHourlyUsageUseCase.kt` | **Completo** | Obtiene uso por hora |
| `UpdateStreakUseCase.kt` | **Completo** | Actualiza racha diaria |
| `AppLimitCardInfo.kt` | **Completo** | Data class para tarjetas de límite en dashboard |

#### `domain/utils/` — Utilidades de dominio

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `GamificationCalculator.kt` | **Completo** | Cálculo de nivel, puntos para siguiente nivel, stats |
| `BadgeDefinitions.kt` | **Completo** | Catálogo de insignias, títulos de nivel, frases motivacionales |

---

### 1.2 Capa `data/` — Implementaciones de Repositorios + SQLDelight

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `GoalsRepositoryImpl.kt` | **Completo** | Implementación SQLDelight de GoalsRepository |
| `GamificationRepositoryImpl.kt` | **Completo** | Implementación SQLDelight de GamificationRepository |
| `WebGoalsRepositoryImpl.kt` | **Completo** | Implementación SQLDelight de WebGoalsRepository (Android) |
| `DevicePreferencesWebGoalsRepository.kt` | **Completo** | Implementación alternativa con DevicePreferences (Web/Desktop) |
| `DatabaseDriverFactory.kt` | **Completo** | Clase `expect` para crear el driver de SQLDelight por plataforma |

```kotlin
// Ejemplo: GoalsRepositoryImpl simplificado
class GoalsRepositoryImpl(private val db: DopamiNahDb) : GoalsRepository {
    private val queries get() = db.dopamiNahDbQueries

    override fun getAllGoals(): Flow<List<AppLimitGoal>> {
        return queries.getAllAppLimitGoals()...map { rows ->
            rows.map { row -> AppLimitGoal(/* mapping */) }
        }
    }
    override suspend fun saveGoal(goal: AppLimitGoal) { ... }
    override suspend fun deleteGoal(id: Int) { ... }
}
```

---

### 1.3 Capa `ui/` — Interfaz de Usuario (Compose Multiplatform)

#### 1.3.1 Navegación

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `Screen.kt` | **Completo** | Sealed class legacy con las rutas de pantallas |
| `AppTab.kt` (en DopamiNahApp.kt) | **Completo** | Enum con tabs del bottom navigation |
| `DopamiNahApp.kt` | **Completo** | Composable raíz con bottom nav y enrutamiento por tab |
| `PermissionState.kt` | **Completo** | Estado de permisos via CompositionLocal |

```kotlin
// DopamiNahApp.kt — Núcleo de navegación
enum class AppTab(val route: String, val title: String) {
    DASHBOARD("dashboard", "Inicio"),
    STATS("stats", "Stats"),
    GOALS("goals", "Metas"),
    WEB("navegacion", "Navegación"),
    ACHIEVEMENTS("achievements", "Logros"),
    SETTINGS("settings", "Ajustes")
}

// Las plataformas ocultan tabs según contexto:
//   - Android: oculta WEB   → setOf(AppTab.WEB)
//   - Web:     oculta DASHBOARD, STATS → setOf(AppTab.DASHBOARD, AppTab.STATS)
```

#### 1.3.2 Pantallas — Estado de Implementación

##### Dashboard (`ui/screens/dashboard/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `DashboardScreen.kt` | **Completo** | Pantalla principal con scroll |
| `DashboardViewModel.kt` | **Completo** | StateFlow: gamification, unlocks, usage, app-limit cards |
| `HeaderSection.kt` | **Completo** | Saludo + stats de racha |
| `StatCard.kt` | **Completo** | Tarjetas de stats individuales |
| `UsageSummaryCarousel.kt` | **Completo** | Carrusel de resumen de uso |
| `MostUsedAppsSection.kt` | **Completo** | Lista de apps más usadas del día |
| `AppUsageItem.kt` | **Completo** | Item individual de app en la lista |
| `AppIconImage.kt` | **Completo** | Carga de íconos de app (Coil) |

##### Stats (`ui/screens/stats/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `StatsScreen.kt` | **Completo** | Pantalla de estadísticas con tabs semanal/mensual |
| `StatsViewModel.kt` | **Completo** | StateFlow con average usage, unlocks, hourly, daily details |
| `StatsHeader.kt` | **Completo** | Encabezado con selector de fecha |
| `StatsSummaryCards.kt` | **Completo** | Tarjetas de resumen (promedio, desbloqueos) |
| `DailyUsageChartCard.kt` | **Completo** | Gráfico de barras de uso diario (Canvas Compose) |
| `AppUsageChartCard.kt` | **Completo** | Gráfico de uso por app (Canvas Compose) |
| `PeakUsageChartCard.kt` | **Completo** | Gráfico de uso por hora (Canvas Compose) |
| `StatsCarousel.kt` | **Completo** | Carrusel horizontal de charts |
| `DailyDetailsCard.kt` | **Completo** | Detalles de un día específico |
| `DatePickerSheet.kt` | **Completo** | Selector de fecha |

##### Goals / App Goals (`ui/screens/goals/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `GoalsScreen.kt` | **Completo** | Pantalla de metas de apps |
| `GoalsViewModel.kt` | **Completo** | CRUD de metas + cálculo de progreso |
| `GoalCard.kt` | **Completo** | Tarjeta de meta individual con progreso |
| `CreateGoalDialog.kt` | **Completo** | Diálogo para crear meta |
| `EditGoalDialog.kt` | **Completo** | Diálogo para editar meta |
| `AddGoalButton.kt` | **Completo** | FAB para agregar meta |
| `GoalsHeader.kt` | **Completo** | Encabezado de la sección |
| `GoalsTipCard.kt` | **Completo** | Tips/consejos |

##### Web Goals (`ui/screens/goals/webgoals/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `WebGoalsScreen.kt` | **Completo** | Pantalla de metas de sitios web |
| `WebGoalsViewModel.kt` | **Completo** | Lógica completa de tracking de dominios, temporizador sincronización con extensión |
| `WebGoalModels.kt` | **Completo** | WebGoal, WebGoalUiModel, WebGoalsState |
| `WebGoalCard.kt` | **Completo** | Tarjeta con barra de progreso |
| `CreateWebGoalDialog.kt` | **Completo** | Diálogo para crear meta web |
| `WebGoalsHeader.kt` | **Completo** | Encabezado |
| `FaviconAvatar.kt` | **Completo** | Avatar con favicon del dominio |
| `BrandIcons.kt` | **Completo** | Iconos de marca para redes sociales conocidas |

```kotlin
// WebGoalsViewModel — Núcleo del tracking web
class WebGoalsViewModel(repository: WebGoalsRepository? = null) {
    // Tracks dominios activos, acumula tiempo, computa bloqueo,
    // sincroniza con extensión vía SyncBridge
    fun addGoal(url: String, timeLimitMinutes: Int, spentMinutes: Int = 0) { ... }
    fun deleteGoal(id: String) { ... }
    fun toggleGoal(id: String) { ... }
    fun notifyVisit(url: String) { ... }    // Register visit on domain
    fun isDomainBlocked(url: String): Boolean { ... }
    private fun startTimer() { ... }  // Timer cada 1s
    private fun rebuildState() { ... }  // Recalcula spent minutes, bloqueos
}
```

##### Achievements (`ui/screens/achievements/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `AchievementsScreen.kt` | **Completo** | Pantalla de logros |
| `AchievementsViewModel.kt` | **Completo** | Carga stats de gamificación y computa badges |
| `AchievementsHeader.kt` | **Completo** | Encabezado decorativo |
| `StreakCard.kt` | **Completo** | Tarjeta de racha actual |
| `LevelCard.kt` | **Completo** | Nivel y progreso |
| `BadgesGrid.kt` | **Completo** | Grid de insignias |
| `NextAchievementCard.kt` | **Completo** | Próximo logro disponible |
| `RewardsSystemCard.kt` | **Completo** | Info del sistema de recompensas |
| `AchievementStatsCard.kt` | **Completo** | Stats: desbloqueadas, total, nivel, mejor racha |

##### Focus Browser / Navegación (`ui/screens/focusbrowser/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `WebNavigationRepository.kt` | **Completo** | Reglas de bloqueo, sesión, stats de navegación |
| `BlockRule.kt` | **Completo** | Modelo de regla de bloqueo |
| `WebStatsScreen.kt` | **Completo** | Pantalla de estadísticas de navegación |
| `BlockedSiteScreen.kt` | **Completo** | Pantalla de sitio bloqueado |

##### Settings (`ui/screens/settings/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `SettingsScreen.kt` | **Completo** | Pantalla de ajustes |
| `SettingsViewModel.kt` | **Completo** | Toggles: notificaciones, modo Pájaro Verde, premium |
| `SettingsSection.kt` | **Completo** | Sección genérica de settings |
| `SettingsToggleItem.kt` | **Completo** | Item con toggle switch |
| `SettingsNavigationItem.kt` | **Completo** | Item que navega a URL |
| `AboutSection.kt` | **Completo** | Sección "Acerca de" |
| `PremiumCard.kt` | **Completo** | Tarjeta para comprar premium |
| `PremiumActiveCard.kt` | **Completo** | Tarjeta de premium activo |

##### WebView (`ui/screens/webview/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `WebViewScreen.kt` | **Completo** | Pantalla webview modal |
| `PlatformWebView.kt` | **Completo** | `expect` para WebView nativo por plataforma |

##### Onboarding (`ui/screens/onboarding/`)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `OnboardingPermissionScreen.kt` | **Completo** | Pantalla de permisos inicial |
| `OnboardingHeader.kt` | **Completo** | Encabezado con logo |
| `OnboardingActionButton.kt` | **Completo** | Botón de acción |
| `PermissionPageContent.kt` | **Completo** | Contenido de página de permiso |

#### 1.3.3 Tema, Iconos y Componentes Compartidos

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `Color.kt` | **Completo** | Paleta de colores (morado, naranja, etc.) |
| `Theme.kt` | **Completo** | DopamiNahTheme con light/dark |
| `Type.kt` | **Completo** | Tipografía |
| `LucideIcons.kt` | **Completo** | Iconos Lucide |
| `LucideExtra.kt` | **Completo** | Iconos adicionales |
| `AppIcon.kt` | **Completo** | Componente de ícono de app (expect/actual) |
| `BrainIcon.kt` | **Completo** | Icono del cerebro (logo) |

---

### 1.4 Utilidades Compartidas

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `Constants.kt` | **Completo** | Constantes globales |
| `DateUtils.kt` | **Completo** | Utilidades de fecha |
| `UsageTimeUtils.kt` | **Completo** | Formateo de tiempo de uso |
| `ImageUtils.kt` | **Completo** | Utilidades de imagen (platform-specific) |
| `Platform.kt` | **Completo** | `expect` functions: platform name, time, preferences, overlay, monitoring service |
| `SyncBridge.kt` | **Completo** | Singleton para comunicación extensión ↔ web app |
| `Greeting.kt` | **Completo** | Saludo |
| `GreetingUtil.kt` | **Completo** | Utilidad de saludo |

---

## 2. Módulo `androidApp/` — Aplicación Android

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `MainActivity.kt` | **Completo** | Entry point Android: permisos, creación de repos, wiring de ViewModels |
| `AppLimitMonitoringService.kt` | **Completo** | Servicio foreground que monitoriza apps en primer plano y muestra overlay de bloqueo cuando se excede el límite |

```kotlin
// MainActivity.kt — Punto de entrada Android
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Crea DB, repos, use cases
        // 2. Wirea PermissionState
        // 3. Crea ViewModels con dependencias Android
        // 4. Llama a App() con hiddenTabs = setOf(AppTab.WEB)
    }
}
```

### Android `actual` implementations

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `Platform.android.kt` | **Completo** | `currentTimeMillis()`, `getPlatformName()`, permisos overlay, monitoring service |
| `DatabaseDriverFactory.android.kt` | **Completo** | Driver SQLite nativo Android |
| `DeviceUsageRepositoryImpl.kt` | **Completo** | Implementación con UsageStatsManager API |
| `PlatformWebView.android.kt` | **Completo** | AndroidView con WebView nativo |
| `AppIcon.android.kt` | **Completo** | Carga de íconos de app con Coil |

---

## 3. Módulo `webApp/` — Aplicación Web (JS / WasmJS)

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `src/webMain/kotlin/main.kt` | **Completo** | Entry point web: register SW, SyncAwareApp, JS interop para sincronización con extensión |
| `resources/index.html` | **Completo** | HTML shell |
| `resources/styles.css` | **Completo** | Estilos CSS |
| `resources/sw.js` | **Completo** | Service Worker |
| `resources/extension-bridge.js` | **Completo** | JS interlayer: message passing con extensión via window.postMessage |

```javascript
// extension-bridge.js — Puente entre Web App y Extensión
window.__dopaminahPostGoals = function(goalsJson) { /* postMessage a extensión */ };
window.__dopaminahPollSync = function() { /* leer pending sync */ };
window.__dopaminahLoadCachedGoals = function() { /* localStorage */ };
window.__dopaminahParseGoalsJson = function(json) { /* parsear goals */ };
```

**Web app difiere de Android en:**
- Oculta tabs DASHBOARD y STATS (no hay UsageStatsManager en web)
- Usa `useWebGoals = true` (solo metas web, no metas de apps)
- Se conecta con la extensión del navegador para sincronizar metas
- Usa `DevicePreferencesWebGoalsRepository` en vez de SQLDelight

---

## 4. Módulo `browser-extension/` — Extensión Chrome/Edge

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `background.js` | **Completo** | Service Worker: tracking de navegación, acumulación de tiempo, DNR rules de bloqueo, mensajería |
| `content.js` | **Completo** | Content script: puente postMessage ↔ chrome.runtime |
| `popup/popup.html` | **Completo** | Popup UI |
| `popup/popup.css` | **Completo** | Estilos del popup |
| `popup/popup.js` | **Completo** | Lógica del popup: CRUD de metas, estado del tab actual |
| `blocked/blocked.html` | **Completo** | Página de sitio bloqueado |
| `blocked/blocked.js` | **Completo** | Lógica de página bloqueada (opción de desbloquear) |

```javascript
// background.js — Service Worker de la extensión
// Usa declarativeNetRequest para bloquear sitios
// Almacena metas y tiempo acumulado en chrome.storage.local
// Sincroniza con la web app via content script → postMessage
```

**Flujo de sincronización extensión ↔ web app:**
```
Extensión (background.js) → chrome.tabs.sendMessage → content.js
→ window.postMessage → extension-bridge.js → SyncBridge
→ WebGoalsViewModel (addGoal, setAccumulatedMinutes)

Web Goals Screen → WebGoalsViewModel.onSyncOut → DopamiNahApp
→ onSyncGoalsToExtension → main.kt sendGoals → extension-bridge.js
→ window.postMessage → content.js → chrome.runtime.sendMessage
→ background.js handleSyncGoals
```

---

## 5. Módulos `iosApp/` y `desktopApp/`

| Archivo | Estado | Propósito |
|---------|--------|-----------|
| `iosMain/Platform.ios.kt` | **Completo** | currentTimeMillis(), getPlatformName() para iOS |
| `iosMain/DatabaseDriverFactory.ios.kt` | **Completo** | Driver SQLite nativo iOS |
| `iosMain/MainViewController.kt` | **Completo** | Entry point iOS |
| `iosMain/PlatformWebView.ios.kt` | **Completo** | UIKit WebView |
| `jvmMain/Platform.jvm.kt` | **Completo** | Implementación desktop |
| `jvmMain/DatabaseDriverFactory.jvm.kt` | **Completo** | Driver SQLite JVM |
| `jvmMain/PlatformWebView.jvm.kt` | **Completo** | JavaFX WebView |
| `jsMain/Platform.js.kt` | **Completo** | Implementación web (JS) |
| `wasmJsMain/Platform.wasmJs.kt` | **Completo** | Implementación web (Wasm) |

---

## 6. Infraestructura y BD

### SQLDelight

Las tablas de la base de datos se definen en archivos `.sq` (no listados pero generados en `build/`):
- `App_limit_goals` — Metas de límite por app
- `Gamification` — Puntos, racha, mejor racha
- `Web_goals` — Metas web (usada en Android)
- `Web_accumulated_minutes` — Minutos acumulados por dominio (usada en Android)

### Dependencias principales

- **Compose Multiplatform 1.7.x** — UI multiplataforma
- **SQLDelight 2.x** — Base de datos local
- **Coil 3.x** — Carga de imágenes
- **Voyager 1.1.x** — Navegación (observado en build output, aunque no se usa directamente en DopamiNahApp.kt)
- **Multiplatform Settings** — Preferences multiplataforma
- **Koin** — DI (observado en build output)
- **kotlinx-datetime** — Fechas

---

## 7. Resumen: ¿Qué está implementado vs placeholder?

| Componente | Implementado | Placeholder | Notas |
|-----------|-------------|-------------|-------|
| **Domain models** | ✅ 100% | — | AppLimitGoal, AppUsageSummary, etc. |
| **Repository interfaces** | ✅ 100% | — | 7 interfaces |
| **Repository implementations** | ✅ 100% | — | SQLDelight + Preferences + Android UsageStats |
| **Use cases** | ✅ 100% | — | 4 use cases completos |
| **Dashboard** | ✅ 100% | — | ViewModel + 7 componentes |
| **Stats screen** | ✅ 100% | — | ViewModel + 8 componentes con gráficos Canvas |
| **App Goals** | ✅ 100% | — | ViewModel + 6 componentes |
| **Web Goals** | ✅ 100% | — | ViewModel + 7 componentes + timer + sync |
| **Achievements** | ✅ 100% | — | ViewModel + 7 componentes |
| **Settings** | ✅ 100% | — | ViewModel + 6 componentes |
| **Focus Browser** | ✅ 100% | — | Repository + 3 screens |
| **Onboarding** | ✅ 100% | — | 1 screen + 3 componentes |
| **WebView** | ✅ 100% | — | expect/actual 5 plataformas |
| **Theme/Colors/Icons** | ✅ 100% | — | Tema completo light/dark |
| **Navegación** | ✅ 100% | — | Bottom nav con tabs ocultables |
| **Android AppLimitMonitoringService** | ✅ 100% | — | Foreground service + overlay bloqueo |
| **Browser Extension** | ✅ 100% | — | background, content, popup, blocked page |
| **Web App sync** | ✅ 100% | — | extension-bridge.js + SyncBridge |
| **AuthRepository** | ✅ Interfaz | ⚠️ Sin implementación concreta | Interfaz + models listos, sin backend real |
| **PremiumRepository** | ✅ Interfaz | ⚠️ Sin implementación concreta | Interfaz + models listos, sin backend real |
| **UsageMonitoringRepository** | ✅ Interfaz | ⚠️ Sin implementación concreta | Interfaz lista, sin implementación |
| **iOS App** | ⚠️ Parcial | ⚠️ | `actual` platform files listos, pero sin entry point completo (solo MainViewController) |
| **Desktop App** | ⚠️ Parcial | ⚠️ | `actual` platform files listos, entry point no visible en source |

---

## 8. Diagrama de Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           ANDROID                                        │
│                                                                          │
│  UsageStatsManager ─► DeviceUsageRepositoryImpl ─► DashboardViewModel   │
│       │                                            StatsViewModel        │
│       ▼                                                       │          │
│  AppLimitMonitoringService ─► GoalsRepositoryImpl            │          │
│       │                           │                         │          │
│       ▼                           ▼                         ▼          │
│  Overlay (bloqueo) ◄── CheckUsageLimitsUseCase ◄── DopamiNahApp         │
│                                                                          │
│  SQLDelight DB ◄── GoalsRepositoryImpl                                   │
│                    GamificationRepositoryImpl                            │
│                    WebGoalsRepositoryImpl (Android)                      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                           WEB APP                                        │
│                                                                          │
│  localStorage ◄── DevicePreferencesWebGoalsRepository                    │
│                        │                                                │
│                        ▼                                                │
│  WebGoalsViewModel ──► DopamiNahApp ──► extension-bridge.js             │
│       │                                      │                          │
│       │                                      ▼                          │
│       └── SyncBridge ◄── extension-bridge.js ◄── content.js             │
│                                                      │                  │
│                                                      ▼                  │
│                                              background.js             │
│                                              (tracking + bloqueo)      │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Conclusión

**No hay placeholders funcionales en la UI o la lógica de negocio.** Todo el código visible en `shared/src/commonMain/kotlin/` está completamente implementado. Las únicas interfaces sin implementación concreta son `AuthRepository`, `PremiumRepository` y `UsageMonitoringRepository`, que definen el contrato pero no tienen implementación `Impl` — probablemente porque dependen de un backend o servicios que aún no están integrados.

La aplicación tiene **3 modos de operación** distintos:
1. **Android**: Dashboard + Stats + App Goals + Achievements + Settings + Web Goals + Focus Browser (AppLimitMonitoringService activo)
2. **Web App**: Web Goals + Focus Browser + Achievements + Settings (sin Dashboard/Stats)
3. **Extensión**: Gestión de metas web + bloqueo DNR + sincronización
