# DopamiNah - Project Documentation

## 1. Project Structure
The project follows a standard modern Android structure employing MVVM and Clean Architecture principles. It is broken down into concise layers.

- **`app/src/main/java/co/edu/unicauca/dopaminah`**
  - `binds/` - Contains Dagger/Hilt bindings and Dependency Injection modules.
  - `data/` - Holds Data layer implementations, communicating with local storages and providing repository concretions.
    - Contains `local/` and `repository/`.
  - `domain/` - Houses Domain layer components, logic interfaces, and robust models.
    - Contains `model/` and `repository/` interfaces.
  - `ui/` - Contains all Jetpack Compose UI code.
    - `components/` - General shared components (e.g., `AppIcon.kt`).
    - `icons/` - Custom or generated SVG/vector icons (Lucide style).
    - `navigation/` - Logic for app routing (`Screen.kt`, `DopamiNahApp.kt`).
    - `screens/` - Represents the individual pages structured by feature domain.
    - `theme/` - Global Theme configurations (Colors, Typography).
  - `utils/` - Utility scripts, extensions, and helper functions.

## 2. Libraries Used
According to `libs.versions.toml` and `build.gradle.kts`, the project relies heavily on the following ecosystem:
- **UI & Architecture:** Jetpack Compose (Material3, Foundation Layout), Activity Compose.
- **Dependency Injection:** Dagger Hilt (`hilt-android`, `hilt-navigation-compose`).
- **Database / Local Storage:** Room (`room-runtime`, `room-ktx`, `room-compiler`), and Android DataStore-Preferences.
- **Navigation:** Jetpack Navigation Compose (`navigation-compose`).
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
