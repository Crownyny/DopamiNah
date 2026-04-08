# DopamiNah — Flujo interno del código

> Monitor de tiempo de pantalla con mecánicas de gamificación estilo Duolingo para Android.

---

## Tabla de contenidos

1. [Arranque de la App](#1-arranque-de-la-app)
2. [Navegación](#2-navegación)
3. [Inyección de Dependencias (Hilt)](#3-inyección-de-dependencias-hilt)
4. [Capa de Datos](#4-capa-de-datos)
5. [ViewModels](#5-viewmodels)
6. [UI (Jetpack Compose)](#6-ui-jetpack-compose)
7. [Flujo end-to-end: Primera apertura](#7-flujo-end-to-end-primera-apertura)
8. [Utilidades](#8-utilidades)
9. [Permisos del sistema](#9-permisos-del-sistema)
10. [Diagrama de capas](#10-diagrama-de-capas)

---

## 1. Arranque de la App

```
Android OS
  └─► DopaminahApplication  (@HiltAndroidApp)
        └─► Inicializa el grafo de dependencias de Hilt (DI global)
              └─► MainActivity  (@AndroidEntryPoint)
```

### `DopaminahApplication.kt`

Sólo una línea relevante: la anotación `@HiltAndroidApp` convierte a esta clase en el punto raíz del contenedor de inyección de dependencias de Hilt. Sin ella, ninguna inyección funcionaría.

### `MainActivity.kt`

- **Splash screen** — `installSplashScreen()` conecta la pantalla de carga nativa de Android 12+ antes de que Compose tome el control.
- **ThemeController** — Se instancia con el `Context` de la aplicación. Expone un `StateFlow<Boolean?>` que indica si el usuario prefirió modo oscuro (guardado en DataStore). Si es `null`, se usa la preferencia del sistema.
- **`enableEdgeToEdge`** — Hace que el contenido se dibuje bajo las barras de sistema, dando el look borde a borde.
- **`setContent`** — Arranca el árbol de Compose:
  - `CompositionLocalProvider(LocalThemeController …)` → inyecta el `ThemeController` en el árbol de Compose para que cualquier pantalla pueda leer/escribir el tema.
  - `DopamiNahTheme(darkTheme)` → aplica el sistema de colores Material 3.
  - `DopamiNahApp()` → punto de entrada de la navegación.

---

## 2. Navegación

### `DopamiNahApp.kt`

```
DopamiNahApp()
  ├─ Comprueba permiso PACKAGE_USAGE_STATS
  │    └─ DeviceUsageRepositoryImpl(context).hasUsageStatsPermission()
  │         → startRoute = "dashboard" | "onboarding_permission"
  ├─ Scaffold
  │    └─ DopamiNahBottomBar  (oculta si está en Onboarding)
  └─ NavHost (Compose Navigation)
       ├─ onboarding_permission  → OnboardingPermissionScreen
       ├─ dashboard              → DashboardScreen
       ├─ stats                  → StatsScreen
       ├─ goals                  → GoalsScreen
       ├─ achievements           → AchievementsScreen
       └─ settings               → SettingsScreen
```

**Decisión de ruta inicial:** Justo al componer `DopamiNahApp`, se llama `hasUsageStatsPermission()` dentro de un bloque `remember {}`. Si el permiso ya fue concedido, el usuario va directo al Dashboard; si no, se le muestra la pantalla de onboarding.

Cuando el usuario concede el permiso, la pantalla de onboarding ejecuta:

```kotlin
navController.navigate(Screen.Dashboard.route) {
    popUpTo(Onboarding) { inclusive = true }
}
```

Esto elimina el onboarding del back-stack para que el botón atrás no regrese a él.

### `Screen.kt`

`sealed class` con objetos `object` por cada ruta. Cada uno lleva: `route: String`, `title: String` (para el label del BottomBar) e `icon: ImageVector` (iconos Lucide personalizados).

---

## 3. Inyección de Dependencias (Hilt)

Hay dos módulos de DI en `binds/`:

### `DatabaseModule.kt`

Provee la instancia de Room `DopaminahDatabase` como `Singleton` global.

### `RepositoryModule.kt`

| Tipo | Repositorio | Implementación |
|------|-------------|----------------|
| `@Binds` (abstract) | `GoalsRepository` | `GoalsRepositoryImpl` |
| `@Binds` (abstract) | `GamificationRepository` | `GamificationRepositoryImpl` |
| `@Provides` (object) | `DeviceUsageRepository` | `DeviceUsageRepositoryImpl(context)` |
| `@Provides` (object) | `ThemeController` | `ThemeController(context)` |

> `DeviceUsageRepository` se provee con `@Provides` en lugar de `@Binds` porque su constructor necesita `Context` explícito y no está anotado con `@Inject`.

---

## 4. Capa de Datos

### 4.1 `DeviceUsageRepositoryImpl` — El núcleo de monitoreo

Es la pieza más compleja. Usa `UsageStatsManager`, la API del sistema Android para leer estadísticas de uso.

**Permiso requerido:** `PACKAGE_USAGE_STATS` (permiso protegido que el usuario debe activar manualmente en Ajustes del sistema).

#### Métodos clave

| Método | Qué hace |
|--------|----------|
| `getDailyUsageStats()` | Agrega todo el uso del día actual (medianoche → ahora) con `queryAndAggregateUsageStats`. Construye `unlockCounts` contando eventos `ACTIVITY_RESUMED` por paquete. Devuelve lista de `AppUsageSummary` ordenada descendente. |
| `getDailyDeviceUnlocks()` | Llama a `countDeviceUnlocks(startOfDay, now)`. |
| `getAverageUsageMillis(days)` | Suma `totalTimeInForeground` de todos los paquetes en los últimos N días, divide entre N. |
| `getDailyUsageForLastDays(days)` | Itera día por día (un `queryAndAggregateUsageStats` por día) → lista de `Long` (ms por día). |
| `getAverageUsagePerApp(days, limit)` | Suma uso por app en el rango, divide entre `days`, retorna top `limit` apps. |
| `getDailyDetails(dayOffset)` | Para un día específico (hoy=0, ayer=1, …): calcula hora del primer uso, sesiones, app más usada, total y desbloqueos. |
| `getHourlyUsage(days)` | Recorre todos los eventos `RESUMED`/`PAUSED` del rango y distribuye la duración de cada sesión en buckets por hora (0–23). Útil para la gráfica de calor horaria. |

#### `countDeviceUnlocks(startTime, endTime)` — Lógica de desbloqueos

```
Recorre UsageEvents:
  eventType == 15 (SCREEN_INTERACTIVE) → count++   ← método primario
  eventType == 18 (KEYGUARD_HIDDEN)    → ignorado para evitar doble conteo
  eventType == 1  (ACTIVITY_RESUMED)
    + gap > 5 min desde último evento  → count++   ← fallback si el dispositivo no emite evento 15
```

#### `distributeDurationToHours(start, end, buckets[])` — Distribución horaria

Algoritmo que divide una sesión que puede cruzar varias horas en fragmentos por bucket:

```
current = start
WHILE current < end:
    hour = current.HOUR_OF_DAY
    nextHourBoundary = inicio de (hour + 1)
    chunkEnd = min(nextHourBoundary, end)
    buckets[hour] += (chunkEnd - current)
    current = chunkEnd
```

---

### 4.2 `GamificationRepositoryImpl` — Racha y puntos

Persiste tres valores en DataStore (clave-valor asíncrono, reemplazo moderno de SharedPreferences):

| Clave | Tipo | Descripción |
|-------|------|-------------|
| `streak` | `Int` | Días consecutivos de apertura |
| `total_points` | `Int` | Puntos acumulados totales |
| `last_opened_timestamp` | `Long` | Timestamp del último lanzamiento (ms) |

#### `incrementStreakAndPoints()` — Lógica de racha diaria

```
diffDays = (hoyMedioNoche - ayerMedioNoche) / 86_400_000

diffDays > 1  → resetear streak a 1, +10 pts  (volvió después de romper racha)
diffDays == 1 → streak++,            +50 pts  (día consecutivo)
diffDays == 0 → no hacer nada                 (ya se abrió hoy)
```

#### `calculateLevel(points)` — Progresión de nivel

Nivel `n` requiere `(n * 100)` pts, con umbral creciente en `+50` por nivel:

```
Lvl 1:   0 pts
Lvl 2: 100 pts
Lvl 3: 250 pts
Lvl 4: 450 pts
…
```

#### `getGamificationStats()` — Flow reactivo

Expone un `Flow<UserGamificationStats>` que emite automáticamente cada vez que DataStore cambia. El ViewModel lo colecta y actualiza la UI.

> **Nota:** Actualmente `currentPoints` en el modelo `UserGamificationStats` transporta el valor de `streak` (comentario en el código: *"Refactor: UI expects currentPoints as current progress, streak could be handled separately"*).

---

### 4.3 `GoalsRepositoryImpl` — Metas de uso (Room)

Delega totalmente al DAO de Room.

#### Entidad Room: `AppLimitGoal`

| Campo | Uso |
|-------|-----|
| `goalType` | `"TOTAL_DAILY"` \| `"APP_LIMIT"` \| `"UNLOCK_LIMIT"` |
| `packageName` | Solo para `APP_LIMIT` |
| `maxTimeMillis` | Límite de tiempo |
| `maxUnlocks` | Límite de desbloqueos |
| `currentStreak` | Días que se cumplió la meta |

#### `GoalsDao` provee

- `getAllGoals()` → `Flow<List<AppLimitGoal>>` (reactivo, Room notifica cambios automáticamente)
- `insertGoal(goal)` — con `OnConflictStrategy.REPLACE`
- `deleteGoal(id)`

---

## 5. ViewModels

### `DashboardViewModel`

**Inyecciones:** `GamificationRepository`, `DeviceUsageRepository`, `GoalsRepository`, `@ApplicationContext`

#### StateFlows expuestos a la UI

| StateFlow | Tipo | Fuente |
|-----------|------|--------|
| `gamificationState` | `UserGamificationStats` | `GamificationRepository.getGamificationStats()` |
| `dailyUnlocks` | `Int` | `DeviceUsageRepository.getDailyDeviceUnlocks()` |
| `yesterdayUnlocks` | `Int` | `DeviceUsageRepository.getYesterdayDeviceUnlocks()` |
| `totalDailyUsageMs` | `Long` | Suma de `getDailyUsageStats()` |
| `dailyUsageStats` | `List<AppUsageSummary>` | `getDailyUsageStats()` (sin la propia app) |
| `appLimitCards` | `List<AppLimitCarouselInfo>` | `combine(goals, usageStats)` |
| `hasUsagePermission` | `Boolean` | `hasUsageStatsPermission()` |

#### `observeAppLimits()` — Reactividad combinada

```kotlin
combine(goalsRepository.getAllGoals(), _dailyUsageStats) { goals, stats ->
    goals.filter { it.goalType == "APP_LIMIT" }
         .map { goal ->
             val usedMs = stats.find { it.packageName == goal.packageName }
                               ?.totalTimeForegroundMillis ?: 0L
             AppLimitCarouselInfo(...)
         }
}.collect { _appLimitCards.value = it }
```

El carrusel de límites se actualiza automáticamente si cambian las metas **o** si cambian las estadísticas de uso.

#### Refresh en `ON_RESUME`

`DashboardScreen` usa `DisposableEffect(lifecycleOwner)` para registrar un observer de ciclo de vida:

```kotlin
if (event == Lifecycle.Event.ON_RESUME) {
    viewModel.checkAndIncrementStreak()
    viewModel.refreshStats()  // → loadUnlockStats()
}
```

Esto garantiza que al volver a la pantalla (p.ej., después de conceder un permiso), las estadísticas se refrescan.

---

### `StatsViewModel`

**Inyección:** sólo `DeviceUsageRepository`

Maneja un único `StateFlow<StatsState>`:

```kotlin
data class StatsState(
    selectedTab: StatsTab,       // WEEKLY | MONTHLY
    dailyAverageText: String,
    unlockAverageText: String,
    lastWeekUsage: List<Float>,  // Horas por día (últimos 7 días)
    appUsageData: List<AppUsageEntry>, // Top apps en el período
    hourlyUsage: List<Float>,    // 24 buckets, minutos promedio por hora
    selectedDayOffset: Int,      // 0=hoy, 1=ayer, …
    dailyDetails: DailyDetailStats?,
    isLoading: Boolean
)
```

El método `loadData()` ejecuta 6 llamadas dentro de una sola corrutina:

1. `getAverageUsageMillis(days)` → texto `"2h 15m/día"`
2. `getAverageUnlocks(days)` → texto `"38/día"`
3. `getDailyUsageForLastDays(7)` → barra de los últimos 7 días
4. `getHourlyUsage(days)` → heatmap horario
5. `getAverageUsagePerApp(days)` → gráfico por app
6. `getDailyDetails(selectedDayOffset)` → tarjeta de detalle del día

---

## 6. UI (Jetpack Compose)

### Jerarquía de pantallas

```
DopamiNahApp
  └─ Scaffold
       ├─ DopamiNahBottomBar  (NavigationBar con 5 ítems)
       └─ NavHost
            ├─ Dashboard
            │    ├─ HeaderSection          (nivel, racha, puntos)
            │    ├─ UsageSummaryCarousel   (tiempo total, desbloqueos, límites por app)
            │    └─ MostUsedAppsSection    (lista de apps más usadas hoy)
            ├─ Stats
            │    ├─ Tab selector (Semanal / Mensual)
            │    ├─ StatsSummaryCards      (promedio diario, desbloqueos)
            │    ├─ AppUsageChartCard      (gráfico top apps)
            │    └─ DailyDetailsCard       (detalle de un día específico)
            ├─ Goals
            ├─ Achievements
            │    └─ AchievementsHeader
            └─ Settings
```

### Patrón de estado Unidireccional (UDF)

```
Repository (fuente de verdad)
    │  Flow / suspend fun
    ▼
ViewModel
    │  StateFlow (inmutable)
    ▼
Screen composable
    │  collectAsState()
    ▼
Content composable (stateless, sólo recibe parámetros)
```

- El **Screen composable** obtiene el ViewModel con `hiltViewModel()` y los estados con `collectAsState()`.
- Llama a un **Content composable** (ej. `DashboardContent`) stateless — facilita el `@Preview` de Compose.
- Los componentes (`HeaderSection`, `UsageSummaryCarousel`, etc.) son funciones `@Composable` puras que reciben datos y lambdas.

---

## 7. Flujo end-to-end: Primera apertura

```
1.  Android inicia DopaminahApplication → Hilt inicializa el grafo DI
2.  Android lanza MainActivity
3.  installSplashScreen() → muestra splash mientras Compose carga
4.  ThemeController lee DataStore → emite null (sin preferencia guardada)
5.  darkTheme = isSystemInDarkTheme()
6.  DopamiNahApp() compone:
    - hasUsageStatsPermission() == false → startRoute = "onboarding_permission"
7.  OnboardingPermissionScreen se muestra
8.  Usuario toca "Conceder permiso" → Settings del sistema
9.  Regresa a la app → onPermissionGranted() →
    navController.navigate("dashboard") { popUpTo("onboarding") { inclusive=true } }
10. DashboardScreen compone → hiltViewModel() crea DashboardViewModel
11. DashboardViewModel.init():
    a. loadGamificationStats() → GamificationRepository.getGamificationStats() (Flow)
       → lee DataStore → streak=0, points=0, level=1
    b. checkAndIncrementStreak() → last_opened=0 → streak=1, +10 pts, guarda timestamp
    c. loadUnlockStats():
       - hasUsageStatsPermission() == true
       - getDailyUsageStats() → UsageStatsManager.queryAndAggregateUsageStats(...)
       - getDailyDeviceUnlocks() → countDeviceUnlocks(...)
       - _dailyUsageStats, _totalDailyUsageMs, etc. actualizados
    d. observeAppLimits():
       - GoalsDao.getAllGoals() → Flow vacío (no hay metas aún)
       - _appLimitCards = []
12. StateFlows emiten nuevo estado → DashboardScreen re-compone
13. UI muestra: nivel 1, racha 1, uso del día, apps más usadas
```

---

## 8. Utilidades

### `UsageTimeUtils`

Objeto singleton con 3 funciones puras:

| Función | Descripción |
|---------|-------------|
| `calculateDiffText(today, yesterday)` | `"↗ +5 vs ayer"` / `"↘ -3 vs ayer"` |
| `calculateTimeDiff(today, yesterday)` | `"↗ +1h 15m vs ayer"` |
| `formatUsageTime(millis)` | `7260000ms → "2h 1m"` |

---

## 9. Permisos del sistema

| Permiso | Para qué |
|---------|----------|
| `PACKAGE_USAGE_STATS` | Leer estadísticas de uso de apps. Permiso protegido: el usuario debe activarlo en Ajustes del sistema (no es `requestPermissions` normal). |
| `POST_NOTIFICATIONS` | Notificaciones push (Android 13+). |
| `QUERY_ALL_PACKAGES` | Resolver nombres de apps de cualquier paquete instalado. |

---

## 10. Diagrama de capas

```
┌─────────────────────────────────────────────┐
│                   UI Layer                  │
│  Screens (Compose)  ←→  ViewModels (Hilt)   │
└──────────────────────┬──────────────────────┘
                       │ interfaces del dominio
┌──────────────────────▼──────────────────────┐
│                 Domain Layer                │
│   DeviceUsageRepository   (interface)       │
│   GamificationRepository  (interface)       │
│   GoalsRepository         (interface)       │
│   Models: AppUsageSummary, AppLimitGoal,    │
│           UserGamificationStats             │
└──────────────────────┬──────────────────────┘
                       │ implementaciones
┌──────────────────────▼──────────────────────┐
│                  Data Layer                 │
│  DeviceUsageRepositoryImpl                  │
│    └─ UsageStatsManager  (Android API)      │
│  GamificationRepositoryImpl                 │
│    └─ DataStore          (Preferences)      │
│  GoalsRepositoryImpl                        │
│    └─ Room DB → GoalsDao → AppLimitGoal     │
└─────────────────────────────────────────────┘
            ▲               ▲
         Hilt DI        Hilt DI
    (RepositoryModule) (DatabaseModule)
```