# DopamiNah - Project Documentation

## 1. Project Structure
The project follows a standard modern Android structure employing MVVM and Clean Architecture principles. It is broken down into concise layers.

- **`app/src/main/java/co/edu/unicauca/dopaminah`**
  - `binds/` - Contains Dagger/Hilt bindings and Dependency Injection modules.
    - `RepositoryModule.kt` - Repository bindings (Goals, Gamification, Usage Monitoring, Auth, Premium).
    - `DatabaseModule.kt` - Room database and DAO providers.
    - `FirebaseModule.kt` - Firebase Auth and Realtime Database providers.
  - `data/` - Holds Data layer implementations, communicating with local storages and providing repository concretions.
    - `local/entity/` - Database entities (e.g., `AppLimitGoalEntity.kt`) separated from domain models.
    - `mapper/` - Data mappers (e.g., `AppLimitGoalMapper.kt`) to convert between Entities and Domain Models.
    - `repository/` - Implementation of repository interfaces using Room, DataStore, and Firebase.
  - `domain/` - Houses Domain layer components, logic interfaces, and robust models.
    - `model/` - Pure Kotlin data classes representing the business state.
    - `repository/` - Interface definitions for data operations.
    - `usecase/` - Centralized business logic (e.g., `CheckUsageLimitsUseCase.kt`, `UpdateStreakUseCase.kt`).
    - `utils/` - Pure domain logic helpers (e.g., `GamificationCalculator.kt`).
  - `ui/` - Contains all Jetpack Compose UI code.
    - `screens/` - Feature-based screens with their respective ViewModels.
    - `components/` - Shared UI elements.
    - `navigation/` - App routing and navigation logic.
    - `theme/` - Visual design system (Color, Type, Theme).
  - `service/` & `worker/` - Background components that delegate logic to the UseCase layer.
  - `utils/` - Android-specific utility helpers (Notifications, Time formatting).

## 2. Libraries Used
According to `libs.versions.toml` and `build.gradle.kts`, the project relies heavily on the following ecosystem:
- **UI & Architecture:** Jetpack Compose (Material3), Hilt for DI, and Coroutines/Flow for reactive data.
- **Database / Local Storage:** Room (with Entity/Model separation) and DataStore-Preferences.
- **Background Tasks:** Foreground Services (UsageMonitoring) and WorkManager (UsageAnalysis).
- **Firebase:** Authentication and Realtime Database (BOM managed).

## 3. Architecture Interaction Flow
The application follows a unidirectional data flow and strict layer separation:

1.  **UI Layer:** ViewModels observe `StateFlow` from UseCases or Repositories and expose state to Composables.
2.  **UseCase Layer:** Orchestrates business logic across multiple repositories (e.g., `CheckUsageLimitsUseCase` combines Goal data with Device Usage data).
3.  **Domain Layer:** Defines the "what" (interfaces and models) without knowing about the "how" (implementation).
4.  **Data Layer:** Implements the "how," using Mappers to ensure database entities don't leak into the domain.

**Key Refactoring Improvements:**
- **Logic Centralization:** Background services and UI now share the same business logic through UseCases.
- **Domain Purity:** Domain models are framework-agnostic (removed Room annotations).
- **API Security:** Service receivers are registered with `RECEIVER_NOT_EXPORTED` for Android 13+ compatibility.

## 4. Pages and Navigation Details
The application implements **6 primary pages/screens**. Routing is actively managed by a `NavHost` configured in `DopamiNahApp.kt`. 

A global bottom navigation bar (`DopamiNahBottomBar`) persists alongside all main sections.

1. **OnboardingPermission**: Gatekeeper for Usage Stats permissions.
2. **Dashboard**: Focal entry point showing summary usage and active app limits.
3. **Stats**: Analytical pane with charts and historical breakdowns.
4. **Goals**: Motivational configuration for setting usage limits.
5. **Achievements**: Gamification center with levels, streaks, and badges.
6. **Settings**: App configuration and Premium status management.

## 5. Authentication & Premium System Architecture
*(The rest of the authentication documentation remains accurate as it already followed a clean abstraction pattern)*

### Overview
The DopamiNah app implements an **optional Firebase Google authentication system** where login is only required to activate premium features. The premium status is persisted in Firebase Realtime Database, with a clean abstraction layer to decouple from Firebase implementation details.
...
