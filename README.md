# DopamiNah

Digital wellness app built with **Kotlin Multiplatform** + **Compose Multiplatform**.
Targets Android, iOS, Desktop (JVM), and Web (JS/Wasm).

---

## Architecture

### Pattern: MVVM (Model-View-ViewModel)

```
┌──────────┐     ┌──────────────┐     ┌───────────┐     ┌──────────┐
│  Screen  │────▶│  ViewModel   │────▶│  UseCase  │────▶│Repository│
│ (Compose)│◀────│ (StateFlow)  │     │           │     │ (iface)  │
└──────────┘     └──────────────┘     └───────────┘     └────┬─────┘
                                                             │
                                                   ┌─────────▼────────┐
                                                   │  Implementation  │
                                                   │  (platform-      │
                                                   │   specific)      │
                                                   └──────────────────┘
```

- **View**: `@Composable` screen functions observe `StateFlow` from ViewModels via `collectAsState()`
- **ViewModel**: Plain Kotlin classes (not `androidx.lifecycle.ViewModel`) with `CoroutineScope(SupervisorJob() + Dispatchers.Main)`. Each tab has its own ViewModel.
- **UseCase**: Thin delegation layer between ViewModel and Repository interfaces
- **Repository**: Interfaces in `domain/repository/`, platform-specific implementations in `data/repository/`
- **No DI framework**: Koin is declared as a dependency but not wired. Dependencies are manually constructed per platform entry point.

### Navigation

Tab-based bottom navigation with 6 tabs. Managed by `AppTab` enum + `mutableStateOf` in `DopamiNahApp.kt`:

| Tab | Route | Screen | Hidden On |
|-----|-------|--------|-----------|
| Inicio | `dashboard` | `DashboardScreen` | Web |
| Stats | `stats` | `StatsScreen` | Web |
| Metas | `goals` | `GoalsScreen` or `WebGoalsScreen` | — |
| Navegación | `navegacion` | `WebStatsScreen` | Android |
| Logros | `achievements` | `AchievementsScreen` | — |
| Ajustes | `settings` | `SettingsScreen` | — |

A `WebViewScreen` overlay renders on top when a URL is opened from Settings.
Permission gating: if `hasUsagePermission == false`, `OnboardingPermissionScreen` is shown instead of `MainContent`.

---

## Project Structure

```
DopamiNah/
├── shared/                          # KMP shared module (all platforms)
│   └── src/
│       ├── commonMain/              # Shared across ALL targets
│       │   ├── composeResources/    # Vector drawables
│       │   └── kotlin/co/edu/unicauca/dopaminah/
│       │       ├── App.kt                       # Root Composable entry point
│       │       ├── Platform.kt                  # expect declarations (time, prefs, services)
│       │       ├── SyncBridge.kt                # Singleton bridge for web↔extension sync
│       │       ├── data/repository/             # Repository implementations
│       │       │   └── GamificationRepositoryImpl.kt
│       │       ├── domain/
│       │       │   ├── model/                   # Data classes (AppLimitGoal, GoalType, etc.)
│       │       │   ├── repository/              # Repository interfaces
│       │       │   ├── usecase/                 # Business logic (CheckUsageLimitsUseCase, etc.)
│       │       │   └── utils/                   # Badge definitions, gamification calculator
│       │       └── ui/
│       │           ├── components/              # Shared composables (AppIcon, BrainIcon)
│       │           ├── icons/                   # Lucide icon set (hand-drawn ImageVectors)
│       │           ├── navigation/              # Scaffold, bottom nav, permission state
│       │           ├── screens/                 # Feature screens
│       │           │   ├── achievements/        # Gamification: level, streak, badges
│       │           │   ├── dashboard/           # Main dashboard with usage summary
│       │           │   ├── focusbrowser/        # Web navigation tracking + blocking
│       │           │   ├── goals/               # App goals + web goals (subsystem)
│       │           │   ├── onboarding/          # Permission onboarding (3 pages)
│       │           │   ├── settings/            # Dark mode, premium, toggles
│       │           │   ├── stats/               # Detailed charts + daily breakdown
│       │           │   └── webview/             # In-app browser (platform-specific)
│       │           ├── theme/                   # Material3 colors, typography, dark/light
│       │           └── utils/                   # Image decoding utility
│       ├── androidMain/             # Android actuals (SharedPreferences, WebView, UsageStats)
│       ├── iosMain/                 # iOS actuals (NSUserDefaults, Safari)
│       ├── jvmMain/                 # Desktop actuals (java.util.prefs)
│       ├── jsMain/                  # Browser JS actuals (in-memory prefs)
│       └── wasmJsMain/              # WASM JS actuals (in-memory prefs)
├── androidApp/                      # Android application
│   └── src/main/
│       ├── kotlin/.../
│       │   ├── MainActivity.kt      # Entry + DI wiring + permission handling
│       │   └── AppLimitMonitoringService.kt  # Foreground service for overlay blocking
│       └── res/
├── desktopApp/                      # Desktop application
│   └── src/main/kotlin/.../main.kt  # Window { App() }
├── webApp/                          # Web application
│   └── src/webMain/
│       ├── kotlin/.../main.kt       # ComposeViewport + JS interop with extension
│       └── resources/               # extension-bridge.js, sw.js, index.html
├── iosApp/                          # iOS Xcode project
│   └── iosApp/
│       ├── iOSApp.swift             # SwiftUI entry
│       └── ContentView.swift        # Bridges to shared MainViewController
├── browser-extension/               # Chrome/Edge extension (Manifest V3)
│   ├── manifest.json, background.js, content.js
│   ├── popup/ (popup.html, popup.js, popup.css)
│   └── blocked/ (blocked.html, blocked.js)
└── docs/
```

---

## Data Persistence

### Platform-Native Key-Value Storage

All persistent state uses **platform-native key-value stores** via `expect/actual DevicePreferences`:

| Platform | Backend | Class |
|----------|---------|-------|
| Android | `SharedPreferences` (`dopaminah_prefs`) | `Platform.android.kt` |
| iOS | `NSUserDefaults` | `Platform.ios.kt` |
| Desktop (JVM) | `java.util.prefs.Preferences` | `Platform.jvm.kt` |
| JS/WasmJS | In-memory `MutableMap<String, String>` | `Platform.js.kt` / `Platform.wasmJs.kt` |

### What Gets Stored

| Data | Keys | How |
|------|------|-----|
| Gamification (streak, points, level) | `streak`, `totalPoints`, `bestStreak`, `lastOpenedDay` | `GamificationRepositoryImpl` → `DevicePreferences` |
| App limit goals | `goal_count`, `goal_N_id`, `goal_N_type`, etc. | `GoalsRepositoryImpl` (Android) → `SharedPreferences` |
| Dark mode | `dark_mode` | `SettingsScreen` toggle |
| Notifications toggle | `notifications_enabled` | `SettingsScreen` toggle |
| Web goals | `web_goal_count`, `web_goal_N_*`, `web_min_N_*` | `WebGoalsRepositoryImpl` → `DevicePreferences` (all platforms) |

**No SQLite / Room / SQLDelight database is used.** All persistence is flat key-value.

### Android-Only Storage

- `UsageStatsManager` — system API for per-app usage stats (read-only)
- `AppLimitMonitoringService` — foreground service running every 1.5s to enforce app limits via overlay

---

## Multiplatform Strategy

### expect/actual Pattern

Platform-specific functionality is declared as `expect` in `commonMain` and implemented as `actual` per source set:

| Feature | expect | Android actual | iOS actual | JVM actual | JS/Wasm actual |
|---------|--------|----------------|------------|------------|----------------|
| Platform name | `getPlatformName()` | `"Android ${SDK}"` | `"iOS ${version}"` | `"Java ${j.version}"` | User agent |
| Current time | `currentTimeMillis()` | `System.cCT` | `NSDate()` | `System.cCT` | `Date.now()` |
| Preferences | `DevicePreferences` | `SharedPreferences` | `NSUserDefaults` | `java.util.prefs` | In-memory map |
| Status bar | `PlatformStatusBarEffect` | Window insets | No-op | No-op | No-op |
| Overlay perm. | `hasOverlayPermission()` | `Settings.canDrawOverlays` | `true` | `true` | `true` |
| Monitoring svc | `start/stopMonitoringService()` | Foreground service | No-op | No-op | No-op |
| WebView | `PlatformWebView` | Android `WebView` | Opens Safari | Stub | Stub |
| App icon | `AppIcon` | `painterResource` | `BrainIcon` | `BrainIcon` | `BrainIcon` |
| Image decode | `ByteArray.decodeToImageBitmap()` | `BitmapFactory` | Skia | Skia | `null` |

Compiler flag: `-Xexpect-actual-classes` enables separate compilation of expect/actual classes.

### Platform Source Sets

```
commonMain → androidMain | iosMain | jvmMain | jsMain | wasmJsMain
```

Each platform source set has its own `Platform.*.kt`, `AppIcon.kt`, `PlatformWebView.kt`, and `ImageUtils.kt`.

### Shared vs. Platform-Specific Code

- **Shared (commonMain)**: All UI (Compose screens), domain models, repository interfaces, use cases, theming, icons
- **Platform-specific**: Storage backend, system service integration (UsageStats, foreground service), WebView, image decoding

---

## Theme

### Material 3 with Custom Colors

**Color.kt** — Full palette:
- **Brand**: `DopaminahPurple` (#8B5CF6), `DopaminahOrange` (#FA832B)
- **Semantic**: `SuccessGreen` (#22C55E), `WarningYellow` (#EAB308), `DangerRed` (#EF4444)
- **Surface**: Light (`BackgroundLight` #F8FAFC, `SurfaceCard` #FFFFFF), Dark (`BackgroundDark` #1C1B1F)
- **ExtendedColors**: `brandPurple`, `brandOrange`, semantic colors — provided via `CompositionLocal`

**Theme.kt** — `DopamiNahTheme` wraps `MaterialTheme` with `DarkColorScheme` / `LightColorScheme` + `ExtendedColors` via `LocalExtendedColors`. Accessible as `MaterialTheme.extendedColors`.

**Type.kt** — Full Material3 `Typography` with custom sizes for all 13 levels (`displayLarge` → `labelSmall`). Font: system sans-serif.

---

## Key Flows

### Web Goals (WebGoalsViewModel)

Self-contained ViewModel managing domain-level time limits with real-time tracking and persistence:

1. **Timer**: `startTimer()` runs a coroutine that calls `rebuildState()` every second and persists accumulated minutes every 30 seconds
2. **Domain tracking**: When user visits a URL via in-app browser, `notifyVisit()` records the domain and starts accumulating time
3. **Persistence**: Goals and accumulated domain time survive app restarts via `WebGoalsRepositoryImpl` → `DevicePreferences` (key-value store)
4. **Blocking**: When `spentMinutes >= dailyTimeLimitMinutes`, the goal's `isBlocked` becomes `true`. For goals with `timeLimitMinutes == 0`, block is immediate.
5. **Sync with browser extension**: Goals are exported via `onSyncOut` callback → serialized JSON → `window.__dopaminahPostGoals()`. Incoming sync from extension arrives via `SyncBridge.onIncomingSync`.

### Browser Extension ↔ Web App Sync

```
Browser Extension (Chrome)           Web App (Wasm/JS)
┌─────────────────┐                 ┌─────────────────────┐
│ background.js    │──postMessage──▶│ extension-bridge.js  │
│ popup.js         │◀───────────────│ (window event)       │
│ content.js       │   JSON goals   │                      │
└─────────────────┘                 │ __dopaminahPollSync()│
                                    │ __dopaminahPostGoals()│
                                    └──────────┬──────────┘
                                               │
                                    ┌──────────▼──────────┐
                                    │ main.kt (Kotlin)    │
                                    │ → SyncBridge        │
                                    │ → WebGoalsViewModel │
                                    └─────────────────────┘
```

### App Limit Monitoring (Android Only)

`AppLimitMonitoringService` runs as a foreground service:
- Polls every 1.5s using `UsageStatsManager.queryUsageStats()`
- Checks current foreground app against saved goals
- If limit exceeded and app is not bypassed: shows full-screen overlay via `WindowManager` + `ComposeView`
- User can "Exit app" (launch home) or "Continue anyway" (add bypass for session)

---

## Build & Run

```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop (with hot reload)
./gradlew :desktopApp:hotRun --auto
./gradlew :desktopApp:run

# Web (Wasm — faster, modern browsers)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Web (JS — older browsers)
./gradlew :webApp:jsBrowserDevelopmentRun

# iOS — open iosApp/ in Xcode
```

### Tests

```bash
./gradlew :shared:jvmTest                    # Desktop
./gradlew :shared:wasmJsTest                 # Web Wasm
./gradlew :shared:jsTest                     # Web JS
./gradlew :shared:iosSimulatorArm64Test      # iOS
./gradlew :shared:testAndroidHostTest        # Android host
```

### Dependencies

| Library | Purpose |
|---------|---------|
| Compose Multiplatform 1.11.0 | UI framework |
| Material3 | Design system |
| Kotlinx Coroutines | Async |
| Coil3 | Image loading (favicons) |
| Multiplatform Settings | Platform-native key-value storage |
| Koin (declared, unused) | Dependency injection |
| Voyager (declared, unused) | Navigation |
| JNA (desktop) | Native OS integration |

---

## Key Design Decisions

- **No DI framework** — Dependencies are manually constructed per platform entry point in `MainActivity.kt` or via `remember { }` in Compose (simpler for a single-dev project)
- **No database** — Flat key-value storage via platform preferences is sufficient for the current data model (gamification stats, flat goal list). SQLDelight/Room would be added if relational queries or offline-first sync were needed.
- **Web goals are ephemeral** — `WebGoalsViewModel` stores goals in-memory only. Persistence comes from syncing with the browser extension (which stores in `chrome.storage`).
- **Card-based grid layout** — The web goals grid uses `BoxWithConstraints` + `IntrinsicSize.Min` for responsive columns with equal-height cards at any screen width.
- **`materialIconsExtended` pinned** — The dependency is deprecated but pinned by the Compose plugin. Replacing it would require rewriting onboarding screens with minimal benefit.
