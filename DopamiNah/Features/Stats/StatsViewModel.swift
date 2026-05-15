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
    var dailyUsageMinutes: [Float] = []
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

    init(deviceUsageRepo: DeviceUsageRepositoryProtocol = ManualDeviceUsageRepository()) {
        self.deviceUsageRepo = deviceUsageRepo
    }

    func loadData() async {
        uiState.isLoading = true

        let days = uiState.selectedTab == .weekly ? 7 : 30

        async let avgUsage = deviceUsageRepo.getAverageUsageMillis(days: days)
        async let avgUnlocks = deviceUsageRepo.getAverageUnlocks(days: days)
        async let dailyData = deviceUsageRepo.getDailyUsageForLastDays(days: days)
        async let appUsage = deviceUsageRepo.getAverageUsagePerApp(days: days, limit: 8)
        async let hourly = deviceUsageRepo.getHourlyUsage(days: days)
        async let details = deviceUsageRepo.getDailyDetails(dayOffset: uiState.selectedDayOffset)

        let avgUsageResult = await avgUsage
        uiState.dailyAverageText = avgUsageResult.formattedUsageTime
        uiState.unlockAverageText = "\(await avgUnlocks)"
        uiState.dailyUsageMinutes = await dailyData.map { Float($0) / 60_000 }
        uiState.appUsageData = await appUsage.map { AppUsageEntry(appName: $0, averageHours: Float($1) / 3_600_000) }
        uiState.hourlyUsage = await hourly
        uiState.dailyDetails = await details

        uiState.isLoading = false
    }

    func selectTab(_ tab: StatsTab) {
        uiState.selectedTab = tab
        uiState.selectedDayOffset = 0
        let days = tab == .weekly ? 7 : 30
        Task {
            uiState.isLoading = true
            async let dailyData = deviceUsageRepo.getDailyUsageForLastDays(days: days)
            async let appUsage = deviceUsageRepo.getAverageUsagePerApp(days: days, limit: 8)
            async let hourly = deviceUsageRepo.getHourlyUsage(days: days)
            async let details = deviceUsageRepo.getDailyDetails(dayOffset: 0)
            async let avgUsage = deviceUsageRepo.getAverageUsageMillis(days: days)
            async let avgUnlocks = deviceUsageRepo.getAverageUnlocks(days: days)

            uiState.dailyUsageMinutes = await dailyData.map { Float($0) / 60_000 }
            uiState.appUsageData = await appUsage.map { AppUsageEntry(appName: $0, averageHours: Float($1) / 3_600_000) }
            uiState.hourlyUsage = await hourly
            uiState.dailyDetails = await details
            uiState.dailyAverageText = (await avgUsage).formattedUsageTime
            uiState.unlockAverageText = "\(await avgUnlocks)"
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
