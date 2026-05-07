import Foundation
import SwiftUI
import Combine

enum StatsTab: String, CaseIterable {
    case weekly = "Semana"
    case monthly = "Mes"
}

struct StatsState {
    var selectedTab: StatsTab = .weekly
    var dailyAverageText: String = "-"
    var unlockAverageText: String = "-"
    var lastWeekUsage: [Float] = []
    var appUsageData: [AppUsageEntry] = []
    var hourlyUsage: [Float] = Array(repeating: 0, count: 24)
    var selectedDayOffset: Int = 0
    var dailyDetails: DailyDetailStats?
    var isLoading: Bool = false
}

@MainActor
final class StatsViewModel: ObservableObject {
    @Published var uiState = StatsState()

    private let deviceUsageRepo: DeviceUsageRepositoryProtocol

    init(deviceUsageRepo: DeviceUsageRepositoryProtocol = MockRepositories.deviceUsage) {
        self.deviceUsageRepo = deviceUsageRepo
    }

    func loadData() async {
        uiState.isLoading = true

        async let avgUsage = deviceUsageRepo.getAverageUsageMillis(days: 7)
        async let avgUnlocks = deviceUsageRepo.getAverageUnlocks(days: 7)
        async let weeklyData = deviceUsageRepo.getDailyUsageForLastDays(days: 7)
        async let appUsage = deviceUsageRepo.getAverageUsagePerApp(days: 7, limit: 8)
        async let hourly = deviceUsageRepo.getHourlyUsage(days: 7)
        async let details = deviceUsageRepo.getDailyDetails(dayOffset: uiState.selectedDayOffset)

        let avgUsageResult = await avgUsage
        uiState.dailyAverageText = avgUsageResult.formattedUsageTime
        uiState.unlockAverageText = "\(await avgUnlocks)"
        uiState.lastWeekUsage = await weeklyData.map { Float($0) / 60_000 }
        uiState.appUsageData = await appUsage.map { AppUsageEntry(appName: $0, averageHours: Float($1) / 3_600_000) }
        uiState.hourlyUsage = await hourly
        uiState.dailyDetails = await details

        uiState.isLoading = false
    }

    func selectTab(_ tab: StatsTab) {
        uiState.selectedTab = tab
        let days = tab == .weekly ? 7 : 30
        Task {
            uiState.isLoading = true
            uiState.lastWeekUsage = await deviceUsageRepo.getDailyUsageForLastDays(days: days).map { Float($0) / 60_000 }
            uiState.appUsageData = await deviceUsageRepo.getAverageUsagePerApp(days: days, limit: 8).map { AppUsageEntry(appName: $0, averageHours: Float($1) / 3_600_000) }
            uiState.hourlyUsage = await deviceUsageRepo.getHourlyUsage(days: days)
            uiState.isLoading = false
        }
    }

    func goToPreviousDay() {
        guard uiState.selectedDayOffset < 30 else { return }
        uiState.selectedDayOffset += 1
        Task {
            uiState.dailyDetails = await deviceUsageRepo.getDailyDetails(dayOffset: uiState.selectedDayOffset)
        }
    }

    func goToNextDay() {
        guard uiState.selectedDayOffset > 0 else { return }
        uiState.selectedDayOffset -= 1
        Task {
            uiState.dailyDetails = await deviceUsageRepo.getDailyDetails(dayOffset: uiState.selectedDayOffset)
        }
    }

    func selectDay(_ dayOffset: Int) {
        uiState.selectedDayOffset = dayOffset
        Task {
            uiState.dailyDetails = await deviceUsageRepo.getDailyDetails(dayOffset: dayOffset)
        }
    }
}
