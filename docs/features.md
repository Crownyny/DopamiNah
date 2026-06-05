# DopamiNah - List of Features

DopamiNah is a cross-platform system designed to help users curb digital addiction by setting time constraints on specific websites and applications. The project is built using Kotlin Multiplatform (KMP) for core application platforms and standard web technologies for the browser extension.

---

## 1. Shared Module (KMP Core)
Located in [`/shared`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/shared/src/commonMain/kotlin), this codebase contains the shared business logic, state models, database tracking, and shared Compose Multiplatform UI screens.

- **Onboarding Screen**: An introductory walkthrough that guides users through configuring their initial goals and permissions.
- **Dashboard Screen**: The central hub displaying current daily limits, active goals, achievements, and statistics.
- **Goals Screen**: Allows creating and editing application/domain limits with options for presets or custom durations.
- **Stats Screen**: Displays usage graphs and analytics detailing time spent per app/website over selected periods.
- **Achievements & Badges Screen**: Gamification system that rewards streaks, successful limit adherence, and focus milestones.
- **Focus Browser / WebView**: An integrated sandboxed web-browsing container that dynamically restricts access when a target site runs out of allowed time.
- **Settings Screen**: Management options including light/dark theme preference toggle, resetting tracking metrics, and backend/web synchronization.

---

## 2. Browser Extension (`web-extension`)
Located in [`/browser-extension`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/browser-extension), this component manages desktop browser usage tracking and declarative network blocking.

### A. Popup UI Interface
- **Current Tab Panel**: Real-time checker displaying the domain name of the active browser tab, today's accumulated active duration on it, and context-dependent button options ("Añadir Límite", "Activar Límite", or "Desactivar Límite").
- **Goals List**: Minimalist flat dashboard summarizing all configured goals with active status badges (Activo, Inactivo, Bloqueado), remaining time calculations, and progress bars.
- **Inline Expandable Form**: Expandable pane to quickly configure domain goals with reactive syncing between preset time choices and a manual numeric input field.
- **Sync with Web App**: Instantly pushes extension limits to the companion web application. Features a 500 ms minimum loading indicator and persists a visual success status showing the exact timestamp of the last sync.
- **System-Adaptive Styling**: Theme matches the Android app's color palette (slate light mode, soft charcoal dark mode) adapting to standard desktop preferences.

### B. Background Engine & Tracking
- **Navigation Monitor**: Hooked to browser events (`chrome.webNavigation`) to track active domains.
- **Time Accumulator**: Automatically tracks active time per domain and persists calculations in storage using periodic alarm tick ticks.
- **Declarative Net Request (DNR) Rules**: Automatically updates dynamic redirection rules to intercept traffic on restricted domains once limits are exceeded.
- **Daily Reset Worker**: Evaluates alarms to automatically flush domain timers at midnight.

### C. Interception Blocked Page
- **Card States**:
  - **Immediate Block**: Shown when zero minutes are allowed for a site.
  - **Time Expired**: Shown when daily usage limit is exceeded. Displays dynamic today vs limit statistics.
- **Unblock for Today Option**: Allows bypassing restrictions for the current day with a validation toast spinner.
- **Visual Design**: Sleek system-adaptive styles (Slate/Charcoal theme) with SVG clock progress graphics.

---

## 3. Android Application (`androidApp`)
Located in [`/androidApp`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/androidApp), compiling shared KMP dependencies into a native APK.

- **Native Entry point**: Loads Compose Multiplatform shared layouts inside an Android Activity lifecycle.
- **Device API Integrations**: Interfaces with system preferences and background managers.

---

## 4. Web Application (`webApp`)
Located in [`/webApp`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/webApp), supporting WebAssembly (Wasm) and Javascript browser runs.

- **Wasm & JS targets**: Offers high-performance rendering of the shared UI in standard browsers.
- **Extension Synchronization Receiver**: Listens for incoming content script messages to import and display extension goals in the web client.

---

## 5. Desktop Application (`desktopApp`)
Located in [`/desktopApp`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/desktopApp), building native binaries for Windows/Mac/Linux.

- **JVM Engine target**: Hosts the multiplatform Compose layout inside a native window container.
- **Hot-Reload capabilities**: Supports active development runs.

---

## 6. iOS Application (`iosApp`)
Located in [`/iosApp`](file:///g:/Universidad/api-moviles/DopamiNah-KMP/iosApp), providing standard entry points for Apple ecosystems.

- **SwiftUI Wrapper**: Mounts the shared Compose Multiplatform UI viewport inside a native iOS view controller.
