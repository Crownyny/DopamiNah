import SwiftUI
import FirebaseCore
import GoogleSignIn
import SwiftData

@main
struct DopamiNahApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject private var themeController = ThemeController()

    var sharedModelContainer: ModelContainer = {
        let schema = Schema([AppLimitGoal.self])
        let modelConfiguration = ModelConfiguration(
            "dopaminahConfiguration",
            url: AppGroupHelper.storeURL,
            allowsSave: true,
            cloudKitDatabase: .none
        )
        do {
            return try ModelContainer(for: AppLimitGoal.self, configurations: modelConfiguration)
        } catch {
            fatalError("Could not create ModelContainer: \(error)")
        }
    }()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(themeController)
                .preferredColorScheme(themeController.currentColorScheme)
        }
        .modelContainer(sharedModelContainer)
    }
}
