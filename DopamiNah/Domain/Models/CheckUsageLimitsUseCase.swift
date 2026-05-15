import Foundation
import SwiftData

@MainActor
struct CheckUsageLimitsUseCase {
    static func execute(modelContext: ModelContext? = nil) async {
        let defaults = AppGroupHelper.defaults
        let manualRepo = ManualDeviceUsageRepository()
        let usageStats = await manualRepo.getDailyUsageStats()
        let totalScreenTime = usageStats.reduce(0) { $0 + $1.totalTimeForegroundMillis }
        let unlockCount = await manualRepo.getDailyDeviceUnlocks()
        let notificationHelper = NotificationHelper.shared

        await checkAppLimits(
            totalScreenTime: totalScreenTime,
            unlockCount: unlockCount,
            defaults: defaults,
            notificationHelper: notificationHelper,
            modelContext: modelContext
        )
        await checkTotalDailyLimit(
            totalScreenTime: totalScreenTime,
            defaults: defaults,
            notificationHelper: notificationHelper
        )
        await checkUnlockLimit(
            unlockCount: unlockCount,
            defaults: defaults,
            notificationHelper: notificationHelper
        )
    }

    private static func checkAppLimits(
        totalScreenTime: Int64,
        unlockCount: Int,
        defaults: UserDefaults,
        notificationHelper: NotificationHelper,
        modelContext: ModelContext?
    ) async {
        guard let modelContext else { return }

        let fetchDescriptor = FetchDescriptor<AppLimitGoal>(
            predicate: #Predicate { $0.goalType == "APP_LIMIT" }
        )
        let appLimitGoals: [AppLimitGoal]
        do {
            appLimitGoals = try modelContext.fetch(fetchDescriptor)
        } catch {
            print("Error fetching app limit goals: \(error)")
            return
        }

        let dailyUsage = await ManualDeviceUsageRepository().getDailyUsageStats()

        for goal in appLimitGoals {
            let usage = dailyUsage.first { $0.appName == goal.appDisplayName }
            let usedMillis = usage?.totalTimeForegroundMillis ?? 0
            let limitMillis = goal.maxTimeMillis

            guard limitMillis > 0 else { continue }

            if usedMillis > limitMillis {
                let alertId = "app_limit_\(goal.id)"
                if !defaults.bool(forKey: "notified_\(alertId)_\(todayString())") {
                    notificationHelper.showNotification(
                        id: NotificationHelper.appUsageNotifID,
                        title: "Límite superado",
                        message: "Has superado el límite de \(goal.appDisplayName) (\(usedMillis.formattedUsageTime) usado de \(limitMillis.formattedUsageTime))",
                        isTimeSensitive: true
                    )
                    defaults.set(true, forKey: "notified_\(alertId)_\(todayString())")
                }
            }

            let unlockLimit = goal.maxUnlocks
            if unlockLimit > 0 && unlockCount > unlockLimit {
                let alertId = "app_unlock_\(goal.id)"
                if !defaults.bool(forKey: "notified_\(alertId)_\(todayString())") {
                    notificationHelper.showNotification(
                        id: NotificationHelper.appUsageNotifID,
                        title: "Desbloqueos excedidos",
                        message: "Has abierto \(goal.appDisplayName) \(unlockCount) veces (límite: \(unlockLimit))",
                        isTimeSensitive: true
                    )
                    defaults.set(true, forKey: "notified_\(alertId)_\(todayString())")
                }
            }
        }
    }

    private static func checkTotalDailyLimit(
        totalScreenTime: Int64,
        defaults: UserDefaults,
        notificationHelper: NotificationHelper
    ) async {
        guard let modelContext = try? ModelContainer(for: AppLimitGoal.self).mainContext else { return }

        let fetchDescriptor = FetchDescriptor<AppLimitGoal>(
            predicate: #Predicate { $0.goalType == "TOTAL_DAILY" }
        )
        guard let goal = try? modelContext.fetch(fetchDescriptor).first,
              goal.maxTimeMillis > 0 else { return }

        if totalScreenTime > goal.maxTimeMillis {
            let alertId = "total_daily"
            if !defaults.bool(forKey: "notified_\(alertId)_\(todayString())") {
                notificationHelper.showNotification(
                    id: NotificationHelper.screenTimeNotifID,
                    title: "Tiempo diario superado",
                    message: "Has superado tu límite de tiempo en pantalla (\(totalScreenTime.formattedUsageTime) usado de \(goal.maxTimeMillis.formattedUsageTime))",
                    isTimeSensitive: true
                )
                defaults.set(true, forKey: "notified_\(alertId)_\(todayString())")
            }
        }
    }

    private static func checkUnlockLimit(
        unlockCount: Int,
        defaults: UserDefaults,
        notificationHelper: NotificationHelper
    ) async {
        guard let modelContext = try? ModelContainer(for: AppLimitGoal.self).mainContext else { return }

        let fetchDescriptor = FetchDescriptor<AppLimitGoal>(
            predicate: #Predicate { $0.goalType == "UNLOCK_LIMIT" }
        )
        guard let goal = try? modelContext.fetch(fetchDescriptor).first,
              goal.maxUnlocks > 0 else { return }

        if unlockCount > goal.maxUnlocks {
            let alertId = "unlock_limit"
            if !defaults.bool(forKey: "notified_\(alertId)_\(todayString())") {
                notificationHelper.showNotification(
                    id: NotificationHelper.unlockCountNotifID,
                    title: "Desbloqueos excedidos",
                    message: "Has desbloqueado el dispositivo \(unlockCount) veces (límite: \(goal.maxUnlocks))",
                    isTimeSensitive: true
                )
                defaults.set(true, forKey: "notified_\(alertId)_\(todayString())")
            }
        }
    }

    private static func todayString() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }
}
