import UIKit
import FirebaseCore
import FirebaseDatabase
import GoogleSignIn
import BackgroundTasks
import SwiftData
import UserNotifications

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()

        GIDSignIn.sharedInstance.restorePreviousSignIn { user, error in
            if let error {
                print("Google Sign-In restore error: \(error.localizedDescription)")
            }
        }

        GamificationManager.shared.checkDailyOpen()

        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: "com.dopaminah.usageAnalysis",
            using: nil
        ) { task in
            Task {
                await self.handleUsageAnalysis(task: task as! BGProcessingTask)
            }
        }

        registerForScreenTimeEvents()

        UNUserNotificationCenter.current().delegate = self

        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        scheduleUsageAnalysis()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        GamificationManager.shared.checkDailyOpen()
        checkDailyReset()
    }

    private func registerForScreenTimeEvents() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleScreenDidTurnOn),
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )
    }

    @objc private func handleScreenDidTurnOn() {
        Task {
            let repo = UsageMonitoringRepositoryImpl()
            await repo.setLastScreenOnTime(Date())
        }
    }

    private func checkDailyReset() {
        Task {
            let repo = UsageMonitoringRepositoryImpl()
            let stats = await repo.getMonitoringStats()
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            let today = formatter.string(from: Date())

            if stats.lastResetDate != today {
                await repo.resetDailyStats()
            }
        }
    }

    private func scheduleUsageAnalysis() {
        let request = BGProcessingTaskRequest(identifier: "com.dopaminah.usageAnalysis")
        request.requiresNetworkConnectivity = false
        request.requiresExternalPower = false
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule usage analysis: \(error)")
        }
    }

    private func handleUsageAnalysis(task: BGProcessingTask) async {
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        do {
            let container = try ModelContainer(for: AppLimitGoal.self, configurations: ModelConfiguration(url: AppGroupHelper.storeURL, allowsSave: true, cloudKitDatabase: .none))
            let context = container.mainContext
            await CheckUsageLimitsUseCase.execute(modelContext: context)
        } catch {
            print("ModelContainer error in background task: \(error)")
            await CheckUsageLimitsUseCase.execute(modelContext: nil)
        }

        scheduleUsageAnalysis()
        task.setTaskCompleted(success: true)
    }
}
