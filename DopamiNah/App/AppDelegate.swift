import UIKit
import FirebaseCore
import FirebaseDatabase
import GoogleSignIn
import BackgroundTasks

class AppDelegate: NSObject, UIApplicationDelegate {
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

        return true
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

        await CheckUsageLimitsUseCase.execute()

        scheduleUsageAnalysis()
        task.setTaskCompleted(success: true)
    }
}
