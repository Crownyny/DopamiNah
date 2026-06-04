# Changelog

## [Unreleased] — dev-kmp

### Added
- Full Settings screen ported from `dev` branch: gradient header with `AppIcon`, premium upsell card (`PremiumCard`) with starred perks list, premium confirmation card (`PremiumActiveCard`), appearance & notifications section (dark mode, notifications, pájaro verde mode toggles with Lucide icons), privacy & security section, support section, and about section with app info.
- `AboutSection` — app info card with `AppIcon`, name, version, and tagline.
- `PremiumActiveCard` — "Eres Premium" confirmation card with star icon.
- `PremiumCard` — premium upsell card with pricing, 5 perks, and action button.
- `SettingsSection` rewritten from bare text label to card wrapper with title header + content slot.
- `SettingsNavigationItem` rewritten with `icon: ImageVector` parameter (was text-only).
- `SettingsToggleItem` rewritten with `icon: ImageVector` parameter and `activeColor` support (was text-only).
- `SettingsViewModel` rewritten with full state: `darkMode`, `notificationsEnabled`, `pajaroVerdeMode`, `isPremium` booleans.
- `LucideSun` and `LucideZap` icons added to `LucideExtra.kt` for light mode and pájaro verde mode icons.
- Full statistics page (`StatsScreen`) ported from `dev` branch: tab selector (semanal/mensual), summary cards (daily avg, unlocks, trend), 3‑chart carousel (usage trend line chart, top apps bar chart, peak hours bar chart), daily detail card with day navigation and date picker sheet.
- `DateUtils.kt` — KMP‑safe day‑of‑week label calculation from epoch time (no `java.util.Calendar` dependency).
- `StatsViewModel` loads real data from `DeviceUsageRepository` — wired through `App` → `DopamiNahApp` → `MainContent` → `StatsScreen(viewModel)`. Created in `MainActivity` with the same `DeviceUsageRepositoryImpl` instance used by the dashboard. (daily usage history, per‑app averages, hourly distribution, daily details) when repository is provided; falls back to demo data.
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
- `DeviceUsageRepositoryImpl.getDailyDetails()` no longer returns hardcoded `"--"` / `0` values for `firstUseTime`, `avgSessionMinutes`, and `unlocks` — now computes real data from `UsageStatsManager.queryEvents()`: first `ACTIVITY_RESUMED` timestamp for `firstUseTime`, tracked resumed→paused pairs for average session duration, and 5‑minute‑gap unlock counting.

### Added (Goals screen)
- `GoalsScreen` ported from `dev` branch: full screen with loading indicator, empty state, and real goal cards with progress bars, edit/delete actions, and exceeded-state warnings.
- `GoalsViewModel` in `commonMain` with real CRUD operations backed by `GoalsRepository` — computes per-goal progress fractions and exceeded flags from `DeviceUsageRepository` daily usage data (total screen time, per-app usage, device unlocks). Exposes `GoalsState` via `StateFlow`.
- `GoalsRepositoryImpl` in `shared/androidMain` — SharedPreferences-backed persistence for `AppLimitGoal` objects (no Room/DataStore required).
- `AddGoalButton`, `CreateGoalDialog`, `EditGoalDialog` composables ported from `dev` branch (accent colors adapted to Material3 theme).
- `GoalCard` rewritten: shows icon box (LucideTimer/LucideSmartphone/LucideLock), title/subtitle, edit/delete icon buttons, exceeded alert row, progress label + percent, and `LinearProgressIndicator` with danger-red coloring when limit is exceeded.
- Goals ViewModel wired through `App` → `DopamiNahApp` → `MainContent` → `GoalsScreen(viewModel)`. Created in `MainActivity` with `GoalsRepositoryImpl`, shared `DeviceUsageRepositoryImpl`, and launcher-app lookup map.

### Added (Achievements screen)
- `AchievementsHeader` — gradient header with "Logros" title, subtitle, and award icon (ported from `dev`).
- `StreakCard` redesigned with gradient orange background, centered fire emoji, streak counter, and dynamic motivational text from `BadgeDefinitions.getStreakMotivation()`.
- `LevelCard` redesigned with gradient background, auto-control level display, streak badge, and emoji indicator based on level.
- `BadgesGrid` updated with locked/unlocked visual distinction: unlocked badges show on purple gradient cards with emoji/title/description; locked badges show a lock icon on muted surface.
- `NextAchievementCard` — shows the next locked badge as a preview card with emoji and description.
- `RewardsSystemCard` — describes the reward system (streak milestones, badge unlocks).
- `AchievementStatsCard` — summary stats: unlocked/total badges, current level, best streak.
- `AchievementsViewModel` rewritten with `AchievementsState`/`BadgeUi` data classes, computes badge unlock status from real `UserGamificationStats` (streak, total points, best streak) via `GamificationRepository`, and identifies the next locked badge.
- Wiring: `AchievementsViewModel` created in `MainActivity` with `gamificationRepo`, passed through `App` → `DopamiNahApp` → `MainContent` → `AchievementsScreen(viewModel)`.
