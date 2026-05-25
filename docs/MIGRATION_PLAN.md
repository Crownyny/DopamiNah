# Plan de Migración a Kotlin Multiplatform (KMP)

## Estado Actual

| Aspecto | Android (`dev-frdy`) | iOS (`ios`) |
|---|---|---|
| **Lenguaje** | Kotlin | Swift |
| **UI** | Jetpack Compose + Material3 | SwiftUI |
| **Arquitectura** | MVVM + Clean Architecture (UseCases) | MVVM + Clean Architecture (Protocols) |
| **DI** | Dagger Hilt | Manual (init defaults) |
| **DB Local** | Room | SwiftData |
| **Auth** | Firebase Auth + Google Sign-In | Firebase Auth + Google/Apple Sign-In |
| **Premium** | Firebase Realtime Database | Firebase Realtime Database |
| **Preferencias** | DataStore | UserDefaults (App Groups) |
| **Monitoreo Uso** | UsageStatsManager | FamilyControls / ScreenTime API |
| **Background** | WorkManager + BootReceiver | BGProcessingTask + DeviceActivity Extensions |
| **Notificaciones** | NotificationCompat | UNUserNotificationCenter |
| **Navegación** | Navigation Compose | NavigationStack + TabView |
| **Nivel mínimo** | SDK 24 | iOS 17+ |

## Estrategia

Migración progresiva: primero el dominio compartido, luego implementaciones de plataforma, y finalmente UI unificada con Compose Multiplatform.

---

## Fase 1: Modelos de Dominio Compartidos (`commonMain`)

Migrar modelos de datos puros (sin dependencias de plataforma) al módulo `shared`.

| Modelo | Origen Android | Origen iOS |
|---|---|---|
| `AppLimitGoal` | `domain/model/AppLimitGoal.kt` | `Domain/Models/AppLimitGoal.swift` |
| `AppUsageSummary` | `domain/model/AppUsageSummary.kt` | `Domain/Models/DomainModels.swift` |
| `UserGamificationStats` | `domain/model/UserGamificationStats.kt` | `Domain/Models/UserModels.swift` |
| `UserPremiumStatus` | `domain/model/UserPremiumStatus.kt` | `Domain/Models/UserModels.swift` |
| `DailyDetailStats` | — (en ViewModel) | `Domain/Models/DomainModels.swift` |
| `AppLimitCardInfo` | `domain/usecase/GetDashboardDataUseCase.kt` | `Domain/Models/DomainModels.swift` |
| `AuthUser` | — (FirebaseUser) | `Domain/Models/UserModels.swift` |

```kotlin
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/domain/model/
```

## Fase 2: Interfaces de Repositorio y Casos de Uso Compartidos (`commonMain`)

Las interfaces (protocolos) de repositorio y los casos de uso con lógica pura van a `commonMain`.

### Repository Interfaces (expect)

| Interfaz | Origen Android | Origen iOS |
|---|---|---|
| `GoalsRepository` | `domain/repository/GoalsRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |
| `DeviceUsageRepository` | `domain/repository/DeviceUsageRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |
| `GamificationRepository` | `domain/repository/GamificationRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |
| `AuthRepository` | `domain/repository/AuthRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |
| `PremiumRepository` | `domain/repository/PremiumRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |
| `UsageMonitoringRepository` | `domain/repository/UsageMonitoringRepository.kt` | `Domain/Repositories/RepositoryProtocols.swift` |

### Use Cases (compartidos directamente)

| Use Case | Origen Android | Origen iOS |
|---|---|---|
| `CheckUsageLimitsUseCase` | `domain/usecase/CheckUsageLimitsUseCase.kt` | `Domain/Models/CheckUsageLimitsUseCase.swift` |
| `GetDashboardDataUseCase` | `domain/usecase/GetDashboardDataUseCase.kt` | — (lógica inline en ViewModel) |
| `GetHourlyUsageUseCase` | `domain/usecase/GetHourlyUsageUseCase.kt` | — |
| `UpdateStreakUseCase` | `domain/usecase/UpdateStreakUseCase.kt` | — (inline en GamificationManager) |

### Utilidades Compartidas

| Utilidad | Origen Android | Origen iOS |
|---|---|---|
| `GamificationCalculator` | `domain/utils/GamificationCalculator.kt` | `Core/Utils/GamificationCalculator.swift` |
| `GamificationManager` | — (lógica en repositorio) | `Core/Utils/GamificationCalculator.swift` |

```kotlin
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/domain/repository/
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/domain/usecase/
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/domain/utils/
```

## Fase 3: Implementaciones de Plataforma (actual/expect)

### Android (`androidMain`)

| Componente | Tecnología KMP | Original Android |
|---|---|---|
| DB Local | SQLDelight (multiplatform) | Room |
| DI | Koin Multiplatform | Dagger Hilt |
| Preferencias | DataStore (ya multiplatform) | DataStore |
| Auth | `actual fun` + Firebase Android SDK | Firebase Auth |
| Premium RTDB | `actual fun` + Firebase Android SDK | Firebase RTDB |
| Monitoreo Uso | `actual fun` + UsageStatsManager | UsageStatsManager |
| Background | WorkManager (solo Android) | WorkManager + BootReceiver |
| Notificaciones | NotificationCompat (solo Android) | NotificationCompat |

### iOS (`iosMain`)

| Componente | Tecnología KMP | Original iOS |
|---|---|---|
| DB Local | SQLDelight (multiplatform) | SwiftData |
| DI | Koin Multiplatform | Manual injection |
| Preferencias | DataStore (multiplatform) | UserDefaults |
| Auth | `actual fun` via Firebase iOS SDK en Swift | Firebase Auth |
| Premium RTDB | `actual fun` via Firebase iOS SDK | Firebase RTDB |
| Monitoreo Uso | `actual fun` + FamilyControls API | FamilyControls / ScreenTime |
| Background | `actual fun` + BGTaskScheduler | BGProcessingTask |
| Notificaciones | UNUserNotificationCenter | UNUserNotificationCenter |
| Screen Time Extensions | **Se mantienen nativas (Swift)** | DeviceActivityExtension |

## Fase 4: Migración de UI a Compose Multiplatform

La UI se unifica gradualmente con Compose Multiplatform.

### Pantallas y sus componentes

| Pantalla | Prioridad | Compose Multiplatform | Android Original (referencia) |
|---|---|---|---|
| `App.kt` (Raíz) | Alta | Navegación + Tema | `ui/navigation/DopamiNahApp.kt` |
| Onboarding | Alta | `OnboardingPermissionScreen.kt` | `ui/screens/onboarding/` |
| Dashboard | Alta | `DashboardScreen.kt` | `ui/screens/dashboard/` |
| Stats | Media | `StatsScreen.kt` | `ui/screens/stats/` |
| Goals | Alta | `GoalsScreen.kt` | `ui/screens/goals/` |
| Achievements | Media | `AchievementsScreen.kt` | `ui/screens/achievements/` |
| Settings | Media | `SettingsScreen.kt` | `ui/screens/settings/` |

### Navegación Compartida

Usar **Voyager** o **Decompose** para navegación multiplatform en lugar de Navigation Compose (Android-only).

```kotlin
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/ui/navigation/
```

### Tema Compartido

```kotlin
// shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/ui/theme/
```

### Iconos

Migrar `LucideIcons.kt` (Android) a Compose Multiplatform.
Los íconos de SF Symbols (iOS) se reemplazan por equivalentes de Material Icons o Lucide.

## Fase 5: Funcionalidad iOS Nativa (Screen Time API)

La API de Screen Time (`FamilyControls`, `DeviceActivity`) **no tiene equivalente en KMP**. Se mantienen como módulos nativos de Swift que se comunican con el `shared` module mediante el framework exportado.

| Componente iOS | Estrategia |
|---|---|
| `DeviceActivityMonitorExtension` | Se mantiene 100% nativo (Swift) |
| `DeviceActivityReportExtension` | Se mantiene 100% nativo (Swift) |
| `ShieldConfigurationExtension` | Se mantiene 100% nativo (Swift) |
| `FamilyControls` authorization | Se mantiene nativo, expone API a shared vía `actual fun` |

## Fase 6: Firebase y Networking

**Estrategia**: Usar Firebase nativo en cada plataforma con una capa de abstracción común.

```kotlin
// commonMain - expect
expect class FirebaseAuthProvider {
    suspend fun signInWithGoogle(): AuthUser?
    suspend fun signOut()
}

// androidMain - actual
actual class FirebaseAuthProvider {
    // Usa Firebase Auth SDK de Android
}

// iosMain - actual
actual class FirebaseAuthProvider {
    // Interop con Firebase iOS SDK via Swift
}
```

Alternativa: **Ktor Client** si se migra Firebase RTDB a una REST API propia.

## Fase 7: Testing

| Tipo | Herramienta |
|---|---|
| Unit tests (common) | `kotlin.test` |
| Android instrumented | `androidHostTest` |
| iOS tests | `iosTest` (Swift) + `iosHostTest` (Kotlin) |

Escribir tests para todos los casos de uso y utilidades compartidas en `commonTest`.

---

## Roadmap

```
Fase 1 (Modelos)
  ├── Migrar AppLimitGoal, AppUsageSummary, UserGamificationStats
  ├── Migrar UserPremiumStatus, DailyDetailStats, AuthUser
  └── Tests unitarios en commonTest

Fase 2 (Repositorios + Use Cases)
  ├── Migrar interfaces de repositorio (expect)
  ├── Migrar casos de uso (CheckUsageLimits, GetDashboardData, etc.)
  ├── Migrar GamificationCalculator
  └── Tests unitarios en commonTest

Fase 3 (Implementaciones Android)
  ├── Configurar SQLDelight (reemplazar Room)
  ├── Migrar DI a Koin (reemplazar Hilt)
  ├── Implementar actual para DeviceUsageRepository (UsageStatsManager)
  ├── Implementar actual para AuthRepository (Firebase Auth)
  ├── Implementar actual para PremiumRepository (Firebase RTDB)
  └── Migrar WorkManager + BootReceiver

Fase 4 (Implementaciones iOS)
  ├── Configurar SQLDelight (reemplazar SwiftData)
  ├── Migrar DI a Koin
  ├── Implementar actual para DeviceUsageRepository (FamilyControls)
  ├── Implementar actual para AuthRepository (Firebase Auth)
  ├── Implementar actual para PremiumRepository (Firebase RTDB)
  └── Implementar actual para UsageMonitoringRepository

Fase 5 (UI Compose Multiplatform)
  ├── Tema compartido (Material3)
  ├── Navegación (Voyager/Decompose)
  ├── OnboardingScreen
  ├── DashboardScreen
  ├── GoalsScreen
  ├── StatsScreen
  ├── AchievementsScreen
  └── SettingsScreen

Fase 6 (iOS Nativo - Screen Time)
  ├── Mantener DeviceActivityMonitorExtension
  ├── Mantener ShieldConfigurationExtension
  ├── Mantener DeviceActivityReportExtension
  └── Bridge con shared module via framework exportado

Fase 7 (Polishing)
  ├── Notificaciones multiplatform
  ├── Deep linking
  ├── Animaciones
  ├── Rendimiento
  └── CI/CD
```

## Estructura de Directorios Propuesta

```
shared/
├── src/
│   ├── commonMain/
│   │   └── kotlin/co/edu/unicauca/dopaminah/
│   │       ├── App.kt                          ← Entry point Compose Multiplatform
│   │       ├── domain/
│   │       │   ├── model/                      ← Modelos compartidos
│   │       │   │   ├── AppLimitGoal.kt
│   │       │   │   ├── AppUsageSummary.kt
│   │       │   │   ├── UserGamificationStats.kt
│   │       │   │   ├── UserPremiumStatus.kt
│   │       │   │   ├── DailyDetailStats.kt
│   │       │   │   └── AuthUser.kt
│   │       │   ├── repository/                 ← Interfaces (expect)
│   │       │   │   ├── GoalsRepository.kt
│   │       │   │   ├── DeviceUsageRepository.kt
│   │       │   │   ├── GamificationRepository.kt
│   │       │   │   ├── AuthRepository.kt
│   │       │   │   ├── PremiumRepository.kt
│   │       │   │   └── UsageMonitoringRepository.kt
│   │       │   ├── usecase/                    ← Casos de uso compartidos
│   │       │   │   ├── CheckUsageLimitsUseCase.kt
│   │       │   │   ├── GetDashboardDataUseCase.kt
│   │       │   │   ├── GetHourlyUsageUseCase.kt
│   │       │   │   └── UpdateStreakUseCase.kt
│   │       │   └── utils/
│   │       │       └── GamificationCalculator.kt
│   │       ├── data/
│   │       │   ├── local/                      ← SQLDelight
│   │       │   └── remote/                     ← Ktor / Firebase
│   │       ├── ui/
│   │       │   ├── navigation/
│   │       │   ├── theme/
│   │       │   ├── components/
│   │       │   └── screens/
│   │       │       ├── onboarding/
│   │       │       ├── dashboard/
│   │       │       ├── stats/
│   │       │       ├── goals/
│   │       │       ├── achievements/
│   │       │       └── settings/
│   │       └── di/                             ← Koin modules
│   │
│   ├── androidMain/
│   │   └── kotlin/co/edu/unicauca/dopaminah/
│   │       ├── data/
│   │       │   ├── DeviceUsageRepositoryImpl.kt
│   │       │   ├── UsageMonitoringRepositoryImpl.kt
│   │       │   ├── GoalsRepositoryImpl.kt (SQLDelight)
│   │       │   ├── AuthRepositoryImpl.kt (Firebase)
│   │       │   ├── PremiumRepositoryImpl.kt (Firebase)
│   │       │   └── GamificationRepositoryImpl.kt
│   │       ├── Platform.android.kt
│   │       ├── worker/                         ← WorkManager
│   │       │   ├── UsageAnalysisWorker.kt
│   │       │   └── BootReceiver.kt
│   │       └── service/
│   │           └── UsageMonitoringService.kt
│   │
│   ├── iosMain/
│   │   └── kotlin/co/edu/unicauca/dopaminah/
│   │       ├── data/
│   │       │   ├── DeviceUsageRepositoryImpl.kt
│   │       │   ├── UsageMonitoringRepositoryImpl.kt
│   │       │   ├── GoalsRepositoryImpl.kt (SQLDelight)
│   │       │   ├── AuthRepositoryImpl.kt (Firebase)
│   │       │   ├── PremiumRepositoryImpl.kt (Firebase)
│   │       │   └── GamificationRepositoryImpl.kt
│   │       ├── Platform.ios.kt
│   │       └── BackgroundTasks.kt
│   │
│   ├── commonTest/
│   ├── androidHostTest/
│   └── iosTest/

androidApp/
└── src/main/
    ├── AndroidManifest.xml
    └── kotlin/co/edu/unicauca/dopaminah/
        ├── MainActivity.kt
        └── DopaminahApplication.kt

iosApp/
├── iosApp/
│   ├── iOSApp.swift
│   ├── ContentView.swift
│   └── ...
├── DeviceActivityMonitor/
├── DeviceActivityReport/
└── ShieldConfiguration/
```

## Dependencias Clave

Agregar a `gradle/libs.versions.toml`:

```toml
[versions]
sqldelight = "2.0.2"
koin = "4.0.2"
ktor = "3.0.3"
voyager = "1.1.0-beta03"
multiplatformSettings = "1.3.0"

[libraries]
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-tabNavigator = { module = "cafe.adriel.voyager:voyager-tab-navigator", version.ref = "voyager" }
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatformSettings" }
```

## Resumen de Decisiones Técnicas

| Decisión | Opción elegida | Alternativas | Razón |
|---|---|---|---|
| DB local multiplatform | SQLDelight | Room (Android-only), Realm | Madurez, generación de esquemas |
| DI multiplatform | Koin | kotlin-inject, Kodein | Simple, buena integración con Compose |
| Networking | Ktor | Retrofit (Android-only) | Multiplatform nativo |
| Navegación | Voyager | Decompose, PreCompose | API simple, tabs nativos |
| Preferencias | DataStore (multiplatform) / multiplatform-settings | DataStore tiene soporte KMP oficial |
| Firebase Auth | Capa de abstracción + SDK nativo | Ktor + REST API | Aprovechar SDKs nativos maduros |
| Screen Time API | Nativo Swift (no KMP) | — | API exclusiva de Apple |
| Gráficos/Charts | Compose custom | Vico, YCharts | Control total, evita dependencias |


## Notas Adicionales

1. **WorkManager vs BGTaskScheduler**: No se pueden unificar; cada plataforma tiene su propio sistema de background.
2. **UsageStatsManager vs FamilyControls**: APIs radicalmente diferentes; la abstracción común debe ser genérica.
3. **Notificaciones**: Las notificaciones locales se pueden abstraer, pero las push requieren FCM (Android) y APNS (iOS).
4. **GamificationManager**: En iOS usa `@AppStorage` con App Groups; en KMP se implementa con multiplatform-settings + DataStore.
5. **BootReceiver**: Solo Android; iOS no tiene equivalente directo.

---

## Apéndice A: Estética Migrada

La estética completa de ambas plataformas ya fue migrada a `shared/src/commonMain/kotlin/co/edu/unicauca/dopaminah/ui/theme/`:

| Archivo | Contenido | Origen Android | Origen iOS |
|---|---|---|---|
| `Color.kt` | Colores base, ExtendedColors, chart gradients | `ui/theme/Color.kt` | `Core/Theme/Color+Theme.swift` |
| `Type.kt` | Tipografía Material3 completa | `ui/theme/Type.kt` | `Core/Theme/Typography.swift` |
| `Theme.kt` | DopamiNahTheme (dark/light schemes) | `ui/theme/Theme.kt` | — |
| `ui/icons/LucideIcons.kt` | 18 íconos Lucide vectoriales | `ui/icons/LucideIcons.kt` | SF Symbols equivalentes |

Los valores de color son **idénticos** en ambas plataformas (mismos hex: #8B5CF6, #FA832B, etc.). En iOS se definen como `Color` extensions de SwiftUI; en KMP se definen como `val` de Compose `Color`. La tipografía usa `system(.rounded, ...)` en iOS y Material3 `TextStyle` en KMP.

## Apéndice B: Monitoreo de Uso en Desktop

### macOS

| Aspecto | Detalle |
|---|---|
| **API** | `NSWorkspace.shared.frontmostApplication` + Accessibility API (AXAPI) |
| **Permiso** | **Accesibilidad**: System Preferences > Security & Privacy > Privacy > Accessibility |
| **Implementación** | JNA/JNI para llamar a `[NSWorkspace sharedWorkspace] frontmostApplication]` |
| **Bundle ID** | `[NSRunningApplication bundleIdentifier]` para identificar la app |
| **Título ventana** | AXAPI: `kAXTitleAttribute` del elemento enfocado |
| **Limitaciones** | Sin permiso de accesibilidad solo se obtiene bundle ID, no título de ventana |
| **Alternativa** | `active-win` (npm) o `work_log` (Rust) como proceso helper |

### Windows

| Aspecto | Detalle |
|---|---|
| **API** | `User32.GetForegroundWindow()` + `GetWindowText()` + `GetWindowThreadProcessId()` |
| **Permiso** | **Ninguno especial** para foreground window |
| **Implementación** | JNA (`com.sun.jna.platform.win32.User32`) |
| **Título ventana** | `GetWindowTextW(hwnd, buffer, length)` |
| **Nombre proceso** | `Kernel32.OpenProcess()` + `Psapi.GetModuleBaseNameW()` |
| **Limitaciones** | UWP apps pueden requerir permisos adicionales |
| **Alternativa** | JNI + Win32 API directa |

### Linux (X11)

| Aspecto | Detalle |
|---|---|
| **API** | `_NET_ACTIVE_WINDOW` (EWMH) + `_NET_WM_NAME` + `_NET_WM_PID` |
| **Permiso** | **Ninguno especial** en X11 |
| **Implementación** | JNA + `libX11.so` (Xlib) o proceso `xdotool getactivewindow getwindowname` |
| **Título ventana** | `XGetWindowProperty(display, window, _NET_WM_NAME)` |
| **Nombre proceso** | Leer `/proc/<pid>/comm` después de obtener PID via `_NET_WM_PID` |
| **Limitaciones** | Requiere X11; no funciona en Wayland |

### Linux (Wayland)

| Aspecto | Detalle |
|---|---|
| **API** | **No disponible**. Wayland no expone el active window por diseño de seguridad |
| **Permiso** | Ninguna API disponible |
| **Alternativas** | `xdg-desktop-portal` (Screencast portal) con intervención del usuario; o usar `libei` (desarrollado por Red Hat) |
| **Recomendación** | Detectar si está en Wayland y mostrar mensaje de funcionalidad limitada, o usar polling de procesos activos |

### Estrategia Recomendada para Desktop en KMP

```kotlin
// expect/actual pattern
expect class DesktopUsageMonitor {
    fun getActiveAppInfo(): ActiveAppInfo?
}

data class ActiveAppInfo(
    val processName: String,
    val windowTitle: String,
    val bundleId: String?  // macOS only
)
```

| Plataforma | Implementación |
|---|---|
| `jvmMain` (Windows) | JNA + User32 |
| `jvmMain` (macOS) | JNA + Cocoa/Foundation + AXAPI |
| `jvmMain` (Linux/X11) | JNA + Xlib |
| `jvmMain` (Linux/Wayland) | Retorna `null` o usa `pgrep` como fallback |

### Tecnologías Útiles para Desktop

- **JNA** (Java Native Access) — acceso a APIs nativas sin JNI
- **JWM** (JetBrains) — window management multiplatform (no expone active window tracking aún)
- **Kotlin Desktop Toolkit** (JetBrains) — futura librería de OS integration
- **`active-win`** (npm) — via Node.js proceso helper si se necesita solución rápida
- **`work_log`** (Rust) — tracking CLI que puede ser invocado como subproceso
