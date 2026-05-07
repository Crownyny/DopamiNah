import Foundation
import SwiftData
import SwiftUI
import Combine

struct GoalsState {
    var goals: [GoalDisplayModel] = []
    var installedApps: [String] = ["Instagram", "TikTok", "X", "WhatsApp", "YouTube", "Spotify", "Chrome", "Maps"]
    var showCreateDialog: Bool = false
    var isLoading: Bool = false
}

@MainActor
final class GoalsViewModel: ObservableObject {
    @Published var state = GoalsState()
    @Query(sort: \AppLimitGoal.id) var storedGoals: [AppLimitGoal]

    private let deviceUsageRepo: DeviceUsageRepositoryProtocol
    private let goalsRepo: GoalsRepositoryProtocol

    init(
        deviceUsageRepo: DeviceUsageRepositoryProtocol = MockRepositories.deviceUsage,
        goalsRepo: GoalsRepositoryProtocol = MockRepositories.goals
    ) {
        self.deviceUsageRepo = deviceUsageRepo
        self.goalsRepo = goalsRepo
    }

    func loadData() async {
        state.isLoading = true
        state.installedApps = await deviceUsageRepo.getDailyUsageStats().map(\.appName)
        await buildDisplayModels()
        state.isLoading = false
    }

    func showCreateGoalDialog() {
        state.showCreateDialog = true
    }

    func hideCreateGoalDialog() {
        state.showCreateDialog = false
    }

    func submitNewGoal(typeLabel: String, appName: String, limitMinutes: Int) async {
        let goal = AppLimitGoal(
            goalType: typeLabel,
            packageName: appName.lowercased().replacingOccurrences(of: " ", with: "."),
            appDisplayName: appName,
            maxTimeMillis: Int64(limitMinutes) * 60_000,
            maxUnlocks: typeLabel == GoalType.unlockLimit ? limitMinutes : 0
        )
        await goalsRepo.saveGoal(goal)
        await buildDisplayModels()
        hideCreateGoalDialog()
    }

    func deleteGoal(id: UUID) async {
        await goalsRepo.deleteGoal(id: id)
        await buildDisplayModels()
    }

    func editGoal(id: UUID, newLimitMinutes: Int) async {
        let goals = await goalsRepo.getAllGoals()
        if var goal = goals.first(where: { $0.id == id }) {
            goal.maxTimeMillis = Int64(newLimitMinutes) * 60_000
            goal.maxUnlocks = goal.goalType == GoalType.unlockLimit ? newLimitMinutes : goal.maxUnlocks
            await goalsRepo.updateGoal(goal)
            await buildDisplayModels()
        }
    }

    func buildDisplayModels() async {
        let goals = await goalsRepo.getAllGoals()
        let todayUsage = await deviceUsageRepo.getDailyUsageStats()

        var displayModels: [GoalDisplayModel] = []

        for (index, goal) in goals.enumerated() {
            let title: String
            let subtitle: String
            let progressLabel: String
            let progressFraction: Float
            let progressPercent: String
            let isExceeded: Bool
            let currentLimitMinutes: Int

            switch goal.goalType {
            case GoalType.totalDaily:
                let totalUsage = todayUsage.reduce(0) { $0 + $1.totalTimeForegroundMillis }
                let limitMinutes = Int(goal.maxTimeMillis / 60_000)
                currentLimitMinutes = limitMinutes
                title = "Tiempo Total Diario"
                subtitle = "Límite de \(limitMinutes)m"
                let fraction = goal.maxTimeMillis > 0 ? Float(totalUsage) / Float(goal.maxTimeMillis) : 0
                progressFraction = min(fraction, 1.0)
                progressPercent = String(format: "%.0f%%", fraction * 100)
                progressLabel = "\(totalUsage.formattedUsageTime) / \(limitMinutes)m"
                isExceeded = totalUsage > goal.maxTimeMillis

            case GoalType.appLimit:
                let appUsage = todayUsage.first { $0.appName == goal.appDisplayName }
                let used = appUsage?.totalTimeForegroundMillis ?? 0
                let limitMinutes = Int(goal.maxTimeMillis / 60_000)
                currentLimitMinutes = limitMinutes
                title = goal.appDisplayName
                subtitle = "Límite de \(limitMinutes)m"
                let fraction = goal.maxTimeMillis > 0 ? Float(used) / Float(goal.maxTimeMillis) : 0
                progressFraction = min(fraction, 1.0)
                progressPercent = String(format: "%.0f%%", fraction * 100)
                progressLabel = "\(used.formattedUsageTime) / \(limitMinutes)m"
                isExceeded = used > goal.maxTimeMillis

            case GoalType.unlockLimit:
                let unlocks = await deviceUsageRepo.getDailyDeviceUnlocks()
                currentLimitMinutes = goal.maxUnlocks
                title = "Límite de Desbloqueos"
                subtitle = "Máximo \(goal.maxUnlocks)"
                let fraction = goal.maxUnlocks > 0 ? Float(unlocks) / Float(goal.maxUnlocks) : 0
                progressFraction = min(fraction, 1.0)
                progressPercent = String(format: "%.0f%%", fraction * 100)
                progressLabel = "\(unlocks) / \(goal.maxUnlocks)"
                isExceeded = unlocks > goal.maxUnlocks

            default:
                currentLimitMinutes = 0
                title = goal.appDisplayName
                subtitle = ""
                progressFraction = 0
                progressPercent = "0%"
                progressLabel = ""
                isExceeded = false
            }

            displayModels.append(GoalDisplayModel(
                id: index,
                goalType: goal.goalType,
                appPackageName: goal.packageName,
                title: title,
                subtitle: subtitle,
                progressLabel: progressLabel,
                progressPercent: progressPercent,
                progressFraction: progressFraction,
                isExceeded: isExceeded,
                currentLimitMinutes: currentLimitMinutes
            ))
        }

        state.goals = displayModels
    }

    var activeGoalsCount: Int {
        storedGoals.count
    }
}
