import Foundation

@MainActor
struct CheckUsageLimitsUseCase {
    static func execute() async {
        let defaults = AppGroupHelper.defaults

        let totalScreenTime = defaults.integer(forKey: "total_screen_time")
        let unlockCount = defaults.integer(forKey: "unlock_count")

        let notificationHelper = NotificationHelper.shared

        await checkAppLimits(notificationHelper: notificationHelper)
        await checkTotalDailyLimit(totalScreenTime: Int64(totalScreenTime), notificationHelper: notificationHelper)
        await checkUnlockLimit(unlockCount: unlockCount, notificationHelper: notificationHelper)
    }

    private static func checkAppLimits(notificationHelper: NotificationHelper) async {
    }

    private static func checkTotalDailyLimit(totalScreenTime: Int64, notificationHelper: NotificationHelper) async {
    }

    private static func checkUnlockLimit(unlockCount: Int, notificationHelper: NotificationHelper) async {
    }

    private static func shouldNotify(alertId: String) -> Bool {
        let today = Calendar.current.startOfDay(for: Date())
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let todayStr = formatter.string(from: today)

        let key = "notified_\(alertId)_\(todayStr)"
        return !AppGroupHelper.defaults.bool(forKey: key)
    }

    private static func markNotified(alertId: String) {
        let today = Calendar.current.startOfDay(for: Date())
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let todayStr = formatter.string(from: today)

        let key = "notified_\(alertId)_\(todayStr)"
        AppGroupHelper.defaults.set(true, forKey: key)
    }
}
