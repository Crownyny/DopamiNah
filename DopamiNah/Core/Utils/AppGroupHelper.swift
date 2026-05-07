import Foundation
import SwiftUI
import Combine

enum AppGroupHelper {
    static let appGroupID = "group.com.dopaminah"

    static var sharedContainerURL: URL {
        FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: appGroupID)
            ?? FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    }

    static var storeURL: URL {
        sharedContainerURL.appendingPathComponent("DopamiNah.sqlite")
    }

    static var defaults: UserDefaults {
        UserDefaults(suiteName: appGroupID) ?? .standard
    }
}

@MainActor
class ThemeController: ObservableObject {
    @Published var isDarkMode: Bool = UserDefaults(suiteName: AppGroupHelper.appGroupID)?.bool(forKey: "is_dark_mode") ?? false {
        didSet {
            UserDefaults(suiteName: AppGroupHelper.appGroupID)?.set(isDarkMode, forKey: "is_dark_mode")
        }
    }

    var currentColorScheme: ColorScheme? {
        isDarkMode ? .dark : .light
    }

    func setDarkMode(_ enabled: Bool) {
        isDarkMode = enabled
    }
}

struct NotificationHelper {
    static let shared = NotificationHelper()

    static let screenTimeNotifID = 1001
    static let unlockCountNotifID = 1002
    static let appUsageNotifID = 1003
    static let appOpenNotifID = 1004
    static let channelID = "usage_alerts"

    func requestAuthorization() async -> Bool {
        do {
            return try await UNUserNotificationCenter.current().requestAuthorization(
                options: [.alert, .badge, .sound, .criticalAlert]
            )
        } catch {
            print("Notification authorization denied: \(error)")
            return false
        }
    }

    func showNotification(id: Int, title: String, message: String, isTimeSensitive: Bool = false) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = message
        content.sound = .defaultCritical

        if isTimeSensitive {
            content.interruptionLevel = .timeSensitive
        }

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: "dopaminah_\(id)",
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error {
                print("Failed to show notification: \(error)")
            }
        }
    }
}
