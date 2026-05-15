import Foundation

final class ManualDeviceUsageRepository: DeviceUsageRepositoryProtocol {
    private let defaults: UserDefaults
    private let storageKey = "manual_usage_data"
    private var cached: StoredUsageData?

    init(defaults: UserDefaults = AppGroupHelper.defaults) {
        self.defaults = defaults
        if read() == nil {
            save(StoredUsageData.default)
        }
    }

    private func read() -> StoredUsageData? {
        if let cached { return cached }
        guard let data = defaults.data(forKey: storageKey) else { return nil }
        cached = try? JSONDecoder().decode(StoredUsageData.self, from: data)
        return cached
    }

    private func save(_ data: StoredUsageData) {
        cached = data
        if let encoded = try? JSONEncoder().encode(data) {
            defaults.set(encoded, forKey: storageKey)
        }
    }

    func invalidateCache() {
        cached = nil
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
        let dailyTotal = stored.apps.reduce(0) { $0 + $1.usageMillis }
        return dailyTotal * Int64(days) / 7
    }

    func getAverageUnlocks(days: Int) async -> Int {
        guard let stored = read() else { return 0 }
        return stored.todayUnlocks * days / 7
    }

    func getDailyUsageForLastDays(days: Int) async -> [Int64] {
        guard let stored = read() else { return Array(repeating: 0, count: days) }
        let baseTotal = stored.apps.reduce(0) { $0 + $1.usageMillis }
        let variations: [Float] = [0.8, 1.1, 0.9, 1.3, 1.15, 0.6, 0.85, 1.2, 0.95, 1.05, 1.4, 0.75, 1.1, 0.7, 1.25, 0.85, 1.3, 0.65, 0.9, 1.35, 1.0, 0.78, 1.18, 0.88, 1.28, 0.72, 1.12, 0.82, 1.22, 0.95]
        return (0..<days).map { i in
            Int64(Float(baseTotal) * variations[i % variations.count])
        }
    }

    func getAverageUsagePerApp(days: Int, limit: Int) async -> [(String, Int64)] {
        guard let stored = read() else { return [] }
        let scale = Float(days) / 7.0
        return stored.apps
            .sorted { $0.usageMillis > $1.usageMillis }
            .prefix(limit)
            .map { ($0.appName, Int64(Float($0.usageMillis) * scale)) }
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
        guard let stored = read() else { return Array(repeating: 0, count: 24) }
        let scale = Float(days) / 7.0
        return stored.hourlyUsage.map { $0 * scale }
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
