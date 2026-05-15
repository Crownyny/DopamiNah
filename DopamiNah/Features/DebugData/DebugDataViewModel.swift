import Foundation
import SwiftUI
import Combine

@MainActor
final class DebugDataViewModel: ObservableObject {
    private let repo: ManualDeviceUsageRepository

    @Published var apps: [StoredAppUsage] = []
    @Published var todayUnlocks: String = ""
    @Published var yesterdayUnlocks: String = ""
    @Published var firstUseTime: String = ""
    @Published var avgSessionMinutes: String = ""
    @Published var selectedHourlyPreset: HourlyPreset = .balanced
    @Published var showAddApp = false
    @Published var showResetAlert = false
    @Published var resetMessage: String?

    enum HourlyPreset: String, CaseIterable {
        case morning = "Mañana"
        case afternoon = "Tarde"
        case evening = "Noche"
        case balanced = "Balanceado"

        var values: [Float] {
            var h = Array(repeating: Float(0), count: 24)
            switch self {
            case .morning:
                h[6] = 10; h[7] = 30; h[8] = 45; h[9] = 35
                h[10] = 25; h[11] = 20; h[12] = 30; h[13] = 20
                h[14] = 15; h[17] = 20; h[18] = 30; h[19] = 25
                h[20] = 20; h[21] = 15
            case .afternoon:
                h[7] = 5; h[8] = 10; h[9] = 15; h[10] = 20
                h[11] = 25; h[12] = 35; h[13] = 40; h[14] = 45
                h[15] = 40; h[16] = 35; h[17] = 30; h[18] = 25
                h[19] = 20; h[20] = 15; h[21] = 10
            case .evening:
                h[7] = 5; h[8] = 10; h[9] = 8; h[12] = 15
                h[13] = 12; h[17] = 20; h[18] = 35; h[19] = 50
                h[20] = 55; h[21] = 45; h[22] = 30; h[23] = 15
            case .balanced:
                h[7] = 15; h[8] = 30; h[9] = 10; h[12] = 25
                h[13] = 40; h[14] = 20; h[18] = 45; h[19] = 55
                h[20] = 50; h[21] = 35; h[22] = 20; h[23] = 10
            }
            return h
        }
    }

    init(repo: ManualDeviceUsageRepository = ManualDeviceUsageRepository()) {
        self.repo = repo
        loadFromRepo()
    }

    func loadFromRepo() {
        let data = repo.getUsageData()
        apps = data.apps
        todayUnlocks = "\(data.todayUnlocks)"
        yesterdayUnlocks = "\(data.yesterdayUnlocks)"
        firstUseTime = data.dailyDetail.firstUseTime
        avgSessionMinutes = "\(data.dailyDetail.avgSessionMinutes)"
    }

    func save() {
        let data = StoredUsageData(
            apps: apps,
            todayUnlocks: Int(todayUnlocks) ?? 0,
            yesterdayUnlocks: Int(yesterdayUnlocks) ?? 0,
            hourlyUsage: selectedHourlyPreset.values,
            dailyDetail: StoredDailyDetail(
                firstUseTime: firstUseTime.isEmpty ? "7:45 AM" : firstUseTime,
                avgSessionMinutes: Int(avgSessionMinutes) ?? 12,
                mostUsedAppName: apps.max(by: { $0.usageMillis < $1.usageMillis })?.appName ?? "Instagram",
                mostUsedAppTime: apps.max(by: { $0.usageMillis < $1.usageMillis }).map { $0.usageMillis.formattedUsageTime } ?? "1h 24m"
            ),
            permissionGranted: true
        )
        repo.setUsageData(data)
        resetMessage = nil
    }

    func resetToDefaults() {
        repo.resetToDefaults()
        loadFromRepo()
        resetMessage = "Datos restaurados a valores por defecto"
    }

    func deleteApp(at offsets: IndexSet) {
        apps.remove(atOffsets: offsets)
    }

    func addApp(name: String, package: String, usageMinutes: Int, unlocks: Int) {
        let app = StoredAppUsage(
            packageName: package,
            appName: name,
            usageMillis: Int64(usageMinutes) * 60_000,
            unlockCount: unlocks
        )
        apps.append(app)
    }
}
