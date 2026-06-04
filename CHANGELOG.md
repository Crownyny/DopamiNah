# Changelog

## [Unreleased] — dev-kmp

### Added
- Daily streak ("racha") feature: `GamificationRepositoryImpl` in `commonMain` uses `DevicePreferences` + `currentTimeMillis()` for streak persistence. Tracks streak count, total points, and best streak using days-since-epoch logic. Reactive via `MutableStateFlow`.
- `expect fun currentTimeMillis(): Long` in `commonMain/Platform.kt` with actual implementations for all 6 targets: Android/JVM (`System.currentTimeMillis()`), iOS (`NSDate`), JS (`Date.now()` via `js()`), WasmJS (`Date.now()` via `js()` with `@OptIn(ExperimentalWasmJsInterop)`).
- `UserGamificationStats` now includes `streak`, `bestStreak`, and `totalPoints` fields.
- `GamificationRepository.checkAndIncrementStreak()` replaces the prior `incrementStreakAndPoints()` — only increments if the last opened day differs from today; resets streak if a day was skipped.
- `DashboardViewModel` now calls `checkAndIncrementStreak()` during `init` (right after subscribing to stats).
- `MainActivity` wires `GamificationRepositoryImpl(DevicePreferences(context))` and `UpdateStreakUseCase`, passing them to `DashboardViewModel`.
- `AchievementsViewModel` accepts an optional `GamificationRepository` and loads real streak/level data from the repository; falls back to defaults if null.
- `StreakCard` now uses `BadgeDefinitions.getStreakMotivation(streak)` for dynamic motivational text.
- App icons in dashboard usage list: each `AppUsageItem` now displays the actual Android app icon instead of text initials. Uses platform-specific image decoding (`BitmapFactory` on Android, Skia on other targets) with a fallback to initials.
- System service filtering in `DeviceUsageRepositoryImpl`: apps without launcher activities and flagged as system services are excluded from daily usage stats. User-facing apps (YouTube, Settings, etc.) are preserved.
- `iconBytes: ByteArray?` field on `AppUsageSummary` data class to carry platform-specific icon data.
- Cross-platform `decodeToImageBitmap()` expect/actual utility for converting `ByteArray` to Compose `ImageBitmap`.
- `AppIconImage` composable: smart icon display that renders the actual app icon or falls back to the first letter of the app name.
- Custom DopamiNah brain iconography ported from `dev` branch: `full_icon.xml`, `dopaminah_icon_foreground.xml`, `dopaminah_icon_monochrome.xml`, `icon_medium.xml`, `icon_animated.xml` (drawables), plus mipmap webp icons and adaptive icon XMLs at all densities.
- `AppIcon` composable converted to `expect`/`actual`: Android uses the custom brain vector drawable (`full_icon.xml`) from shared module resources; other platforms keep the "DN" placeholder.
- Android launcher now uses the custom `dopaminah_icon` instead of generic `ic_launcher`.

### Changed
- `DeviceUsageRepositoryImpl.getDailyUsageStats()` now loads each app's icon as PNG bytes and filters out background system services.
- `AppUsageItem` updated to use `AppIconImage` for displaying app icons.
- `AndroidManifest.xml`: icon/roundIcon references updated to `@mipmap/dopaminah_icon`.
- `AppIcon` refactored from inline composable to `expect`/`actual` pattern across all 6 KMP targets.
- `HeaderSection` now displays the real brain icon on Android.

### Fixed
- N/A
