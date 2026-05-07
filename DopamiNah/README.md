# DopamiNah — iOS (Swift / SwiftUI)

App de Salud Digital para la desintoxicación de dopamina tecnológica. Versión nativa iOS re-creada desde el proyecto Android original.

---

## Requisitos

- **Xcode 15+** (Swift 5.9+)
- **iOS 17.0+** mínimo
- **macOS Sonoma 14+** para desarrollo
- Cuenta de **Apple Developer** ($99/año) — requerida para Family Controls
- **Firebase Project** configurado con Auth + Realtime Database

---

## Configuración del Proyecto

### 1. Crear el proyecto en Xcode

1. Abrir Xcode → `File > New > Project...` → `iOS > App`
2. Configuración:
   - **Product Name:** `DopamiNah`
   - **Organization Identifier:** `com.dopaminah`
   - **Interface:** SwiftUI
   - **Language:** Swift
   - **Minimum Deployment:** iOS 17.0
3. Copiar todos los archivos de esta carpeta en la estructura correspondiente.

### 2. Activar Capabilities

En el target principal (`DopamiNah`), activar:

| Capability | Entitlement | Por qué |
|------------|-------------|---------|
| **App Groups** | `group.com.dopaminah` | Compartir datos entre app y extensiones |
| **Family Controls** | `com.apple.developer.family-controls` | Screen Time API |
| **Sign in with Apple** | `com.apple.developer.authentication-services.autofill-credential-provider` | Paridad visual con Google |
| **Push Notifications** | — | Alertas de límites |
| **Background Modes** | `background-processing` | Análisis de uso en segundo plano |

### 3. Crear Extension Targets

Agregar 3 nuevos targets al proyecto:

#### A. Device Activity Monitor Extension
- `File > New > Target` → `Device Activity Monitor Extension`
- Product Name: `DeviceActivityMonitorExtension`
- Bundle ID: `com.dopaminah.DeviceActivityMonitorExtension`

#### B. Device Activity Report Extension
- `File > New > Target` → `Device Activity Report Extension`
- Product Name: `DeviceActivityReportExtension`
- Bundle ID: `com.dopaminah.DeviceActivityReportExtension`

#### C. Shield Action Extension
- `File > New > Target` → `Shield Action Extension`
- Product Name: `ShieldConfigurationExtension`
- Bundle ID: `com.dopaminah.ShieldConfigurationExtension`

### 4. Agregar Dependencias (Swift Package Manager)

`File > Add Package Dependencies`:

| Package | URL |
|---------|-----|
| **Firebase iOS SDK** | `https://github.com/firebase/firebase-ios-sdk.git` |
| **GoogleSignIn** | `https://github.com/google/GoogleSignIn-iOS.git` |

Seleccionar los productos: `FirebaseAuth`, `FirebaseDatabase`, `GoogleSignIn`.

### 5. Configurar GoogleService-Info.plist

1. Descargar `GoogleService-Info.plist` desde la consola de Firebase
2. Arrastrar al proyecto en Xcode (marcar "Copy items if needed")
3. Colocar en el target principal

### 6. Configurar App Groups

1. Ir a `Signing & Capabilities` → **App Groups**
2. Agregar `group.com.dopaminah` en TODOS los targets (app + 3 extensiones)

### 7. Solicitar Family Controls Entitlement

**Antes de enviar a App Store:**

1. Ir a `https://developer.apple.com`
2. Certificates, Identifiers & Profiles → Identifiers
3. Seleccionar el App ID → Edit → Capabilities
4. Activar **Family Controls**
5. Justificar: "Digital Wellbeing / Health Tool"
6. Enviar solicitud de aprobación (puede tardar 1–2 semanas)

---

## Estructura de Archivos

```
DopamiNah-swift/
├── App/
│   ├── DopamiNahApp.swift              ← Entry point + Firebase + SwiftData
│   ├── AppDelegate.swift               ← BGTasks, Google Sign-In restore
│   ├── Info.plist                      ← URL schemes, background tasks
│   ├── DopamiNah.entitlements          ← Family Controls, App Groups
│   ├── PrivacyInfo.xcprivacy           ← Privacy manifest (Apple 2024+)
│   └── GoogleService-Info.plist        ← Firebase config
├── Core/
│   ├── Theme/
│   │   ├── Color+Theme.swift           ← Paleta exacta del Design System
│   │   └── Typography.swift            ← SF Pro, Dynamic Type, spacing
│   ├── Extensions/
│   │   └── SwiftExtensions.swift       ← Color(hex:), Date, View helpers
│   └── Utils/
│       ├── UsageTimeUtils.swift        ← Formateo de tiempo
│       ├── GamificationCalculator.swift ← Niveles, puntos, rachas
│       └── AppGroupHelper.swift        ← App Groups URL, UserDefaults
├── Domain/
│   ├── Models/
│   │   ├── AppLimitGoal.swift          ← SwiftData @Model
│   │   ├── DomainModels.swift          ← AppUsageSummary, BadgeUi, etc.
│   │   ├── UserModels.swift            ← AuthUser, UserPremiumStatus, Stats
│   │   └── CheckUsageLimitsUseCase.swift
│   └── Repositories/
│       └── RepositoryProtocols.swift   ← Protocolos para inyección
├── Data/
│   ├── Local/
│   │   └── SwiftDataGoalsRepository.swift
│   ├── Firebase/
│   │   ├── AuthRepositoryImpl.swift    ← Google + Apple Sign-In
│   │   ├── AppleSignInDelegate.swift
│   │   └── PremiumRepositoryImpl.swift ← Realtime Database
│   └── RepositoriesImpl/
│       ├── DeviceUsageRepositoryMock.swift ← Mock para simulador
│       └── MockRepositories.swift
├── Features/
│   ├── ContentView.swift               ← TabView principal (5 tabs)
│   ├── Onboarding/
│   │   └── OnboardingView.swift        ← 3 páginas de permisos
│   ├── Dashboard/
│   │   ├── DashboardView.swift         ← Header, carousel, apps
│   │   └── DashboardViewModel.swift
│   ├── Stats/
│   │   ├── StatsView.swift             ← Charts con Swift Charts
│   │   └── StatsViewModel.swift
│   ├── Goals/
│   │   ├── GoalsView.swift             ← CRUD de metas
│   │   └── GoalsViewModel.swift
│   ├── Achievements/
│   │   └── AchievementsView.swift      ← Rachas, niveles, insignias
│   └── Settings/
│       ├── SettingsView.swift          ← Auth, premium, toggles
│       └── SettingsViewModel.swift
└── Extension Targets/
    ├── DeviceActivityMonitor/          ← Vigilancia de metas en background
    ├── DeviceActivityReport/           ← Gráficas sandboxed (Charts)
    └── ShieldConfiguration/            ← Pantalla de bloqueo
```

---

## Arquitectura

**MVVM + Clean Architecture:**

```
View (SwiftUI) → ViewModel (@Observable) → Repository Protocol → Repository Impl → Data Source
```

- **SwiftUI** con `@Observable` (Swift 5.9+) reemplaza `StateFlow` + `collectAsState()`
- **SwiftData** reemplaza Room DB para metas de apps
- **UserDefaults + App Groups** reemplaza DataStore Preferences
- **Firebase Auth + Realtime DB** reemplaza Firebase Android SDK
- **Family Controls** reemplaza `UsageStatsManager` (no existe equivalente directo en iOS)
- **BGProcessingTask** reemplaza WorkManager

---

## Reglas de Gamificación

| Acción | Puntos |
|--------|--------|
| Abrir la app (1 vez/día) | +10 |
| Cumplir todas las metas del día | +50 |
| Racha (gap < 48h) | Incrementa |
| Racha (gap >= 48h) | Reset a 0 |

**Nivel:** `nivel = totalPoints / 500`

---

## Notas Críticas para App Store

### Privacy Manifest
`PrivacyInfo.xcprivacy` ya está incluido. Documenta:
- Uso de UserDefaults (`CA92.1`)
- Sin tracking de terceros

### App Tracking Transparency
Si en el futuro agregas Firebase Analytics o AdMob, debes:
1. Agregar `NSUserTrackingUsageDescription` al Info.plist
2. Llamar `ATTrackingManager.requestTrackingAuthorization()` antes de cualquier tracking

### Family Controls Approval
Apple es extremadamente estricto con este API. Sin la aprobación del entitlement `com.apple.developer.family-controls`, la app será rechazada automáticamente.

### Sign in with Apple
Obligatorio por guideline 4.8 del App Store Review si ofreces login con Google. Ambas opciones deben tener paridad visual.

---

## Ejecutar en Simulador

1. El `DeviceUsageRepositoryMock` provee datos de prueba
2. Los ViewModels usan `MockRepositories` por defecto
3. Para probar con datos reales necesitas un dispositivo físico con Family Controls activado

```bash
# Build
xcodebuild -scheme DopamiNah -destination 'platform=iOS Simulator,name=iPhone 15' build

# Run tests (cuando se agreguen)
xcodebuild test -scheme DopamiNah -destination 'platform=iOS Simulator,name=iPhone 15'
```

---

## Migración desde Android

| Android | iOS |
|---------|-----|
| Jetpack Compose | SwiftUI |
| Material Design 3 | Custom Design System (SF Pro) |
| Room DB | SwiftData |
| DataStore Preferences | UserDefaults + App Groups |
| `UsageStatsManager` | Family Controls (DeviceActivity) |
| WorkManager | BGProcessingTask |
| Foreground Service | DeviceActivityMonitor Extension |
| Hilt DI | Manual / Swift 6 Dependency Injection |
| Flow + StateFlow | `@Observable` + `@Published` |
| Navigation Compose | NavigationStack + TabView |
| Firebase Android SDK | Firebase iOS SDK (SPM) |
| Credential Manager API | GoogleSignIn-iOS + AuthenticationServices |

---

## Créditos

Proyecto original: **DopamiNah** (Android/Kotlin/Jetpack Compose)
Universidad: Universidad del Cauca — Ingeniería de Software
Migración iOS: Swift 5.9+ / SwiftUI / iOS 17+
