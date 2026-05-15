import Foundation

final class UsageMonitoringRepositoryImpl: UsageMonitoringRepositoryProtocol {
    private let defaults: UserDefaults

    init(defaults: UserDefaults = AppGroupHelper.defaults) {
        self.defaults = defaults
    }

    private enum Keys {
        static let totalScreenTime = "total_screen_time"
        static let unlockCount = "unlock_count"
        static let lastResetDate = "last_reset_date"
        static let lastScreenOnTime = "last_screen_on_time"
    }

    func getMonitoringStats() async -> MonitoringStats {
        let totalMillis = Int64(defaults.integer(forKey: Keys.totalScreenTime))
        let unlocks = defaults.integer(forKey: Keys.unlockCount)
        let lastReset = defaults.string(forKey: Keys.lastResetDate) ?? ""
        return MonitoringStats(
            totalScreenTimeMillis: totalMillis,
            unlockCount: unlocks,
            lastResetDate: lastReset
        )
    }

    func updateScreenTime(_ millis: Int64) async {
        let current = defaults.integer(forKey: Keys.totalScreenTime)
        defaults.set(current + Int(millis), forKey: Keys.totalScreenTime)
    }

    func incrementUnlockCount() async {
        let current = defaults.integer(forKey: Keys.unlockCount)
        defaults.set(current + 1, forKey: Keys.unlockCount)
    }

    func resetDailyStats() async {
        defaults.set(0, forKey: Keys.totalScreenTime)
        defaults.set(0, forKey: Keys.unlockCount)
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        defaults.set(formatter.string(from: Date()), forKey: Keys.lastResetDate)

        let allKeys = defaults.dictionaryRepresentation().keys
        for key in allKeys where key.hasPrefix("notified_") {
            defaults.removeObject(forKey: key)
        }
    }

    func setLastScreenOnTime(_ date: Date) async {
        defaults.set(date.timeIntervalSince1970, forKey: Keys.lastScreenOnTime)
    }

    func getLastScreenOnTime() async -> Date? {
        let timestamp = defaults.double(forKey: Keys.lastScreenOnTime)
        guard timestamp > 0 else { return nil }
        return Date(timeIntervalSince1970: timestamp)
    }

    func isAlertNotified(alertId: String) async -> Bool {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let today = formatter.string(from: Date())
        let key = "notified_\(alertId)_\(today)"
        return defaults.bool(forKey: key)
    }

    func markAlertNotified(alertId: String) async {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let today = formatter.string(from: Date())
        let key = "notified_\(alertId)_\(today)"
        defaults.set(true, forKey: key)
    }
}
