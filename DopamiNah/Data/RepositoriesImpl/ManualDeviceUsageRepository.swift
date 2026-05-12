import Foundation

@MainActor
final class ManualDeviceUsageRepository: DeviceUsageRepositoryProtocol {
    private let defaults: UserDefaults
    private let storageKey = "manual_usage_data"

    init(defaults: UserDefaults = AppGroupHelper.defaults) {
        self.defaults = defaults
        if read() == nil {
            save(StoredUsageData.default)
        }
    }

    private func read() -> StoredUsageData? {
        guard let data = defaults.data(forKey: storageKey) else { return nil }
        return try? JSONDecoder().decode(StoredUsageData.self, from: data)
    }

    private func save(_ data: StoredUsageData) {
        if let encoded = try? JSONEncoder().encode(data) {
            defaults.set(encoded, forKey: storageKey)
        }
    }

    func hasUsageStatsPermission() async -> Bool {
        read()?.permissionGranted ?? true
    }

    func getDailyUsageStats() async -> [AppUsageSummary] {
        guard let stored = read() else { return [] }
        return stored.apps.map {
            AppUsageSummary(
                packageName: $0.packageName,
                appName: $0.appName,
                totalTimeForegroundMillis: $0.usageMillis,
                unlockCount: $0.unlockCount,
                lastTimeUsed: Date()
            )
        }
    }

    func getDailyDeviceUnlocks() async -> Int {
        read()?.todayUnlocks ?? 0
    }

    func getYesterdayDeviceUnlocks() async -> Int {
        read()?.yesterdayUnlocks ?? 0
    }

    func getAverageUsageMillis(days: Int) async -> Int64 {
        guard let stored = read() else { return 0 }
        let total = stored.apps.reduce(0) { $0 + $1.usageMillis }
        return total
    }

    func getAverageUnlocks(days: Int) async -> Int {
        read()?.todayUnlocks ?? 0
    }

    func getDailyUsageForLastDays(days: Int) async -> [Int64] {
        guard let stored = read() else { return Array(repeating: 0, count: days) }
        let total = stored.apps.reduce(0) { $0 + $1.usageMillis }
        return Array(repeating: total, count: days)
    }

    func getAverageUsagePerApp(days: Int, limit: Int) async -> [(String, Int64)] {
        guard let stored = read() else { return [] }
        return stored.apps
            .sorted { $0.usageMillis > $1.usageMillis }
            .prefix(limit)
            .map { ($0.appName, $0.usageMillis) }
    }

    func getDailyDetails(dayOffset: Int) async -> DailyDetailStats {
        guard let stored = read() else { return .placeholder }
        let date = Date().addingDays(-dayOffset)
        let total = stored.apps.reduce(0) { $0 + $1.usageMillis }
        return DailyDetailStats(
            dateLabel: date.dayLabel,
            firstUseTime: stored.dailyDetail.firstUseTime,
            avgSessionMinutes: stored.dailyDetail.avgSessionMinutes,
            mostUsedAppName: stored.dailyDetail.mostUsedAppName,
            mostUsedAppTime: stored.dailyDetail.mostUsedAppTime,
            unlocks: stored.todayUnlocks,
            totalTimeMillis: total
        )
    }

    func getHourlyUsage(days: Int) async -> [Float] {
        read()?.hourlyUsage ?? Array(repeating: 0, count: 24)
    }

    func checkUsageLimits() async -> [AppLimitCardInfo] {
        guard let stored = read() else { return [] }
        return stored.apps.filter { $0.usageMillis > 0 }.map {
            AppLimitCardInfo(
                packageName: $0.packageName,
                appName: $0.appName,
                timeUsedMs: $0.usageMillis,
                timeLimitMs: $0.usageMillis > 3_600_000 ? $0.usageMillis - 1_440_000 : $0.usageMillis + 1_800_000
            )
        }
    }

    // MARK: - Public API for debug view
    func setUsageData(_ data: StoredUsageData) {
        save(data)
    }

    func getUsageData() -> StoredUsageData {
        read() ?? .default
    }

    func resetToDefaults() {
        save(.default)
    }
}
