# DopamiNah - Project Documentation

## 1. Project Structure
The project follows a standard modern Android structure employing MVVM and Clean Architecture principles. It is broken down into concise layers.

- **`app/src/main/java/co/edu/unicauca/dopaminah`**
  - `binds/` - Contains Dagger/Hilt bindings and Dependency Injection modules.
    - `RepositoryModule.kt` - Repository bindings (Goals, Gamification, Usage Monitoring, Auth, Premium).
    - `DatabaseModule.kt` - Room database and DAO providers.
    - `FirebaseModule.kt` - Firebase Auth and Realtime Database providers.
  - `data/` - Holds Data layer implementations, communicating with local storages and providing repository concretions.
    - Contains `local/` and `repository/`.
    - **`repository/AuthRepositoryImpl.kt`** - Firebase authentication implementation.
    - **`repository/PremiumRepositoryImpl.kt`** - Firebase Realtime Database premium status management.
  - `domain/` - Houses Domain layer components, logic interfaces, and robust models.
    - Contains `model/` and `repository/` interfaces.
    - **`model/UserPremiumStatus.kt`** - Premium status data model.
    - **`model/AuthUser.kt`** - Authenticated user data model.
    - **`repository/AuthRepository.kt`** - Authentication interface (sign-in, sign-out, current user).
    - **`repository/PremiumRepository.kt`** - Premium status interface (get/set premium status).
  - `ui/` - Contains all Jetpack Compose UI code.
    - `components/` - General shared components (e.g., `AppIcon.kt`).
    - `icons/` - Custom or generated SVG/vector icons (Lucide style).
    - `navigation/` - Logic for app routing (`Screen.kt`, `DopamiNahApp.kt`).
    - `screens/` - Represents the individual pages structured by feature domain.
      - **`settings/components/GoogleSignInDialog.kt`** - Google Sign-In composable dialog.
    - `theme/` - Global Theme configurations (Colors, Typography).
  - `utils/` - Utility scripts, extensions, and helper functions.

## 2. Libraries Used
According to `libs.versions.toml` and `build.gradle.kts`, the project relies heavily on the following ecosystem:
- **UI & Architecture:** Jetpack Compose (Material3, Foundation Layout), Activity Compose.
- **Dependency Injection:** Dagger Hilt (`hilt-android`, `hilt-navigation-compose`).
- **Database / Local Storage:** Room (`room-runtime`, `room-ktx`, `room-compiler`), and Android DataStore-Preferences.
- **Navigation:** Jetpack Navigation Compose (`navigation-compose`).
- **Firebase:** Firebase Authentication, Firebase Realtime Database (BOM for version management).
- **Google Play Services:** Google Sign-In API for OAuth authentication.
- **Accompanist:** Drawablepainter.
- **Core Android Plugins:** Core KTX, Lifecycle Runtime KTX, Core SplashScreen.
- **Testing:** JUnit4, Espresso Core, and Compose UI Test integrations.

## 3. Pages and Navigation Details
The application implements **6 primary pages/screens**. Routing is actively managed by a `NavHost` configured in `DopamiNahApp.kt`. 

A global bottom navigation bar (`DopamiNahBottomBar`) persists alongside all main sections.

1. **OnboardingPermission** (`Screen.OnboardingPermission`): Designed as an initial gatekeeper, requiring the user to grant necessary usage context permissions.
2. **Dashboard** (`Screen.Dashboard`): Main app focal entry point, showing summary usage, statistics, leading applications used, and app usage carousels.
3. **Stats** (`Screen.Stats`): An analytical pane depicting full app breakdowns, daily details maps, peak usage charts, alongside configurable date thresholds via `DatePickerSheet`.
4. **Goals** (`Screen.Goals`): A motivational configuration pane, authorizing the user to add and maintain goals.
5. **Achievements** (`Screen.Achievements`): Extends functionality with gamification aspects, tracking level progress, streaks, rewards logic, and unlocking badges.
6. **Settings** (`Screen.Settings`): Basic configuration, allowing generic app customizations (Premium status flips, layout adjustments, about segments).

## 4. Composables Used and Architecture Interaction
The development architecture strictly follows a declarative and heavily compositional pattern. Within `ui/screens` alone, around **46 distinct UI files/composables** handle drawing the application layouts in an isolated and decoupled way.

**Notable Composable Domains:**
- **Navigation Components:** `DopamiNahBottomBar` inside the `Scaffold`.
- **Dashboard Feature Set:** `HeaderSection`, `StatCard`, `MostUsedAppsSection`, `UsageSummaryCarousel`, `AppUsageItem`.
- **Statistics Viewport:** `StatsHeader`, `StatsSummaryCards`, `DailyUsageChartCard`, `PeakUsageChartCard`, `AppUsageChartCard`, `DatePickerSheet`.
- **Goal Formats:** `GoalsHeader`, `GoalsTipCard`, `GoalCard`, `AddGoalButton`, `CreateGoalDialog`, `EditGoalDialog`.
- **Gamification / Achievements:** `AchievementsHeader`, `StreakCard`, `LevelCard`, `BadgesGrid`, `NextAchievementCard`, `RewardsSystemCard`, `AchievementStatsCard`.
- **Basic Configuration:** `SettingsSection`, `SettingsNavigationItem`, `SettingsToggleItem`, `PremiumCard`, `AboutSection`.

**Interaction Flow:**
The majority of screens depend directly on an injected `ViewModel` (`SettingsViewModel`, `StatsViewModel`, etc., backed by `HiltViewModel`). Each underlying screen acts passively, absorbing states from models and communicating intents strictly as event-based lambda closures (e.g., `onClick = { }`).
When routing triggers happen outside the scope of lower modules, the `NavController` invokes `navController.navigate()` efficiently to re-draw and cache backstack operations correctly.

## 5. Authentication & Premium System Architecture

### Overview
The DopamiNah app implements an **optional Firebase Google authentication system** where login is only required to activate premium features. The premium status is persisted in Firebase Realtime Database, with a clean abstraction layer to decouple from Firebase implementation details.

### Key Components

#### Domain Layer (Abstractions)
- **`AuthRepository`** - Interface defining authentication operations:
  - `signInWithGoogle(idToken: String)` - Authenticate with Google ID token
  - `signOut()` - Sign out current user
  - `getCurrentUser()` - Get current authenticated user
  - `currentUser: Flow<AuthUser?>` - Reactive current user state

- **`PremiumRepository`** - Interface defining premium status management:
  - `getPremiumStatus(userId: String)` - Observe premium status in real-time
  - `setPremiumStatus(userId: String, isPremium: Boolean)` - Activate/deactivate premium
  - `isPremiumUser(userId: String)` - Check if user is premium (one-time read)

#### Data Layer (Firebase Implementation)
- **`AuthRepositoryImpl`** - Firebase Authentication implementation:
  - Uses Firebase Auth for Google Sign-In via `GoogleAuthProvider.getCredential()`
  - Maintains a `StateFlow<AuthUser?>` for reactive user state
  - Handles authentication errors and returns `Result<T>` types for safe error propagation

- **`PremiumRepositoryImpl`** - Firebase Realtime Database implementation:
  - Stores premium data at path `/premiumUsers/{userId}`
  - Uses `callbackFlow` for real-time premium status updates
  - Fallback mechanism returns non-premium status on errors

#### UI Layer
- **`GoogleSignInDialog`** - Composable dialog for Google Sign-In:
  - Displays sign-in interface with error messaging
  - Integrates Google Sign-In API with callback handling
  - Shows loading state during authentication

- **`SettingsScreen`** - Integrates auth and premium flows:
  - Shows `PremiumCard` with login button (when not premium)
  - Shows `PremiumActiveCard` (when premium status active)
  - Displays "Account" section with user info and sign-out option
  - Shows error notifications for auth failures

- **`SettingsViewModel`** - Orchestrates auth and premium logic:
  - Observes `currentUser` flow from `AuthRepository`
  - Observes premium status from `PremiumRepository`
  - Handles sign-in/sign-out/premium activation
  - Manages error states and user notifications

### Firebase Realtime Database Schema
```
/premiumUsers
  /{userId}
    isPremium: boolean
    activationDate: number (timestamp)
    expiryDate: number | null (optional)
```

### Dependency Injection
- **`FirebaseModule`** - Provides Firebase instances:
  - `FirebaseAuth.getInstance()` - Singleton auth instance
  - `FirebaseDatabase.getInstance()` - Singleton database instance

- **`RepositoryModule`** - Binds abstractions to implementations:
  - `AuthRepository` → `AuthRepositoryImpl`
  - `PremiumRepository` → `PremiumRepositoryImpl`

### Important Configuration Notes
**Before building, ensure you have:**
1. Firebase project set up in Firebase Console
2. `google-services.json` placed in `app/` directory
3. Google Sign-In Web Client ID configured in `GoogleSignInDialog.kt` (replace `YOUR_WEB_CLIENT_ID`)
4. Firebase Realtime Database enabled and configured with appropriate security rules

### Premium Status Flow
1. User clicks "Iniciar sesión para desbloquear" on PremiumCard
2. GoogleSignInDialog opens and initiates Google Sign-In
3. On successful sign-in, `SettingsViewModel.signInWithGoogle()` is called with idToken
4. `AuthRepository` authenticates with Firebase using the idToken
5. On auth success, `activatePremium()` calls `PremiumRepository.setPremiumStatus()`
6. Premium status is written to Firebase Realtime Database
7. Real-time listener in `PremiumRepository.getPremiumStatus()` detects change
8. UI updates to show `PremiumActiveCard`
9. Notification shows success message to user
