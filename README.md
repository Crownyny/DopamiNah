# DopamiNah 📱⚡

DopamiNah is a screen time monitor that detects social media overuse. Using Duolingo-style psychological pressure—intense notifications, streaks, and rewards—it forces you to slash phone usage. The goal: combat digital procrastination and reclaim your daily productivity.

## ✨ Core Features
- **Usage Dashboard:** Real-time summary and tracking of app usage patterns.
- **Detailed Analytics:** Visual charts depicting daily and peak usage, giving deep insights into your habits.
- **Goals & Limits:** Set strict usage goals and monitor limits to reduce screen time.
- **Gamification & Rewards:** Build streaks, unlock badges, and earn rewards based on reduced phone usage to stay on track.
- **Smart Permissions:** Specialized Android onboarding guiding you to authorize Usage Stats seamlessly.

## 🛠️ Technology Stack
- **UI Framework:** Jetpack Compose (Material3) 
- **Dependency Injection:** Dagger Hilt
- **Local Storage:** Room Database and DataStore-Preferences
- **Navigation:** Jetpack Navigation Compose
- **Architecture:** MVVM and Clean Architecture

## 📂 Project Structure
Built with Android's recommended modern approach:
- **`data/`**: Local implementations (Room) and Repositories.
- **`domain/`**: Models and core business logic.
- **`ui/`**: Jetpack Compose separated into screens, generic components, and navigation routes.

## 📖 Documentation
For comprehensive details on all the pages, composables, and structures used in the project, please consult our full [Project Documentation](docs/project_documentation.md).

## 🚀 Getting Started
1. Open up the project in Android Studio.
2. Allow Android Studio to sync the Gradle dependencies (`libs.versions.toml`).
3. Build and Run the app on any device with **Android 7.0 (Nougat, API 24)** or newer. Ensure usage stats permissions are granted during onboarding!