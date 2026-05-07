import SwiftUI

struct ContentView: View {
    @AppStorage("onboarding_completed", store: AppGroupHelper.defaults) var onboardingCompleted = false
    @State private var selectedTab = 0

    var body: some View {
        if !onboardingCompleted {
            OnboardingView()
        } else {
            MainTabView(selectedTab: $selectedTab)
        }
    }
}

struct MainTabView: View {
    @Binding var selectedTab: Int

    var body: some View {
        TabView(selection: $selectedTab) {
            DashboardView()
                .tabItem {
                    Label("Inicio", systemImage: "house.fill")
                }
                .tag(0)

            StatsView()
                .tabItem {
                    Label("Stats", systemImage: "chart.bar.fill")
                }
                .tag(1)

            GoalsView()
                .tabItem {
                    Label("Metas", systemImage: "scope")
                }
                .tag(2)

            AchievementsView()
                .tabItem {
                    Label("Logros", systemImage: "rosette")
                }
                .tag(3)

            SettingsView()
                .tabItem {
                    Label("Ajustes", systemImage: "gearshape.fill")
                }
                .tag(4)
        }
        .tint(.dopaminahPurple)
    }
}
