import Foundation
import SwiftData
import SwiftUI
import Combine

struct GoalsState {
    var goals: [GoalDisplayModel] = []
    var installedApps: [String] = StoredUsageData.allAppsList.map(\.name)
    var showCreateDialog: Bool = false
    var isLoading: Bool = false
}

@MainActor
final class GoalsViewModel: ObservableObject {
    @Published var state = GoalsState()

    private let deviceUsageRepo: DeviceUsageRepositoryProtocol

    init(deviceUsageRepo: DeviceUsageRepositoryProtocol = ManualDeviceUsageRepository()) {
        self.deviceUsageRepo = deviceUsageRepo
    }

    func loadData(modelContext: ModelContext) async {
        state.isLoading = true
        state.installedApps = await deviceUsageRepo.getDailyUsageStats().map(\.appName)
        await refreshGoals(modelContext: modelContext)
        state.isLoading = false
    }

    func showCreateGoalDialog() {
        state.showCreateDialog = true
    }

    func hideCreateGoalDialog() {
        state.showCreateDialog = false
    }

    func submitNewGoal(typeLabel: String, appName: String, limitMinutes: Int, modelContext: ModelContext) async {
        let goal = AppLimitGoal(
            goalType: typeLabel,
            packageName: appName.lowercased().replacingOccurrences(of: " ", with: "."),
            appDisplayName: appName,
            maxTimeMillis: Int64(limitMinutes) * 60_000,
            maxUnlocks: typeLabel == GoalType.unlockLimit ? limitMinutes : 0
        )
        modelContext.insert(goal)
        try? modelContext.save()
        await refreshGoals(modelContext: modelContext)
        hideCreateGoalDialog()
    }

    func deleteGoal(id: UUID, modelContext: ModelContext) async {
        let fetch = FetchDescriptor<AppLimitGoal>(predicate: #Predicate { $0.id == id })
        if let goal = try? modelContext.fetch(fetch).first {
            modelContext.delete(goal)
            try? modelContext.save()
        }
        await refreshGoals(modelContext: modelContext)
    }

    func editGoal(id: UUID, newLimitMinutes: Int, modelContext: ModelContext) async {
        let fetch = FetchDescriptor<AppLimitGoal>(predicate: #Predicate { $0.id == id })
        if let goal = try? modelContext.fetch(fetch).first {
            goal.maxTimeMillis = Int64(newLimitMinutes) * 60_000
            goal.maxUnlocks = goal.goalType == GoalType.unlockLimit ? newLimitMinutes : goal.maxUnlocks
            try? modelContext.save()
        }
        await refreshGoals(modelContext: modelContext)
    }

    private func refreshGoals(modelContext: ModelContext) async {
        let fetch = FetchDescriptor<AppLimitGoal>(sortBy: [SortDescriptor(\.id)])
        let goals = (try? modelContext.fetch(fetch)) ?? []

        let todayUsage = await deviceUsageRepo.getDailyUsageStats()
        let dailyUnlocks = await deviceUsageRepo.getDailyDeviceUnlocks()
        let totalUsage = todayUsage.reduce(0) { $0 + $1.totalTimeForegroundMillis }

        updateGoalStreaks(goals, totalUsage: totalUsage, dailyUnlocks: dailyUnlocks, todayUsage: todayUsage, modelContext: modelContext)

        state.goals = buildDisplayModels(from: goals, totalUsage: totalUsage, dailyUnlocks: dailyUnlocks, todayUsage: todayUsage)
    }

    private func updateGoalStreaks(_ goals: [AppLimitGoal], totalUsage: Int64, dailyUnlocks: Int, todayUsage: [AppUsageSummary], modelContext: ModelContext) {
        let now = Date()
        let todayStart = Calendar.current.startOfDay(for: now)
        for goal in goals {
            let isMet = checkGoalMet(goal, totalUsage: totalUsage, dailyUnlocks: dailyUnlocks, todayUsage: todayUsage)
            if let lastUpdate = goal.lastStreakUpdateDate, Calendar.current.isDate(lastUpdate, inSameDayAs: now) {
                continue
            }
            goal.currentStreak = isMet ? goal.currentStreak + 1 : 0
            goal.lastStreakUpdateDate = todayStart
        }
        try? modelContext.save()
    }

    private func checkGoalMet(_ goal: AppLimitGoal, totalUsage: Int64, dailyUnlocks: Int, todayUsage: [AppUsageSummary]) -> Bool {
        switch goal.goalType {
        case GoalType.totalDaily:
            return totalUsage <= goal.maxTimeMillis
        case GoalType.appLimit:
            let appUsage = todayUsage.first { $0.appName == goal.appDisplayName }
            let used = appUsage?.totalTimeForegroundMillis ?? 0
            return used <= goal.maxTimeMillis
        case GoalType.unlockLimit:
            return dailyUnlocks <= goal.maxUnlocks
        default:
            return true
        }
    }

    private func buildDisplayModels(from goals: [AppLimitGoal], totalUsage: Int64, dailyUnlocks: Int, todayUsage: [AppUsageSummary]) -> [GoalDisplayModel] {
        guard !goals.isEmpty else { return [] }

        return goals.map { goal in
            let title: String
            let subtitle: String
            let progressLabel: String
            let progressFraction: Float
            let isExceeded: Bool
            let currentLimitMinutes: Int

            switch goal.goalType {
            case GoalType.totalDaily:
                let limitMinutes = Int(goal.maxTimeMillis / 60_000)
                currentLimitMinutes = limitMinutes
                title = "Tiempo Total Diario"
                subtitle = "Límite de \(limitMinutes)m"
                let fraction = goal.maxTimeMillis > 0 ? Float(totalUsage) / Float(goal.maxTimeMillis) : 0
                progressFraction = min(fraction, 1.0)
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
                progressLabel = "\(used.formattedUsageTime) / \(limitMinutes)m"
                isExceeded = used > goal.maxTimeMillis

            case GoalType.unlockLimit:
                currentLimitMinutes = goal.maxUnlocks
                title = "Límite de Desbloqueos"
                subtitle = "Máximo \(goal.maxUnlocks)"
                let fraction = goal.maxUnlocks > 0 ? Float(dailyUnlocks) / Float(goal.maxUnlocks) : 0
                progressFraction = min(fraction, 1.0)
                progressLabel = "\(dailyUnlocks) / \(goal.maxUnlocks)"
                isExceeded = dailyUnlocks > goal.maxUnlocks

            default:
                currentLimitMinutes = 0
                title = goal.appDisplayName
                subtitle = ""
                progressFraction = 0
                progressLabel = ""
                isExceeded = false
            }

            return GoalDisplayModel(
                id: goal.id,
                goalType: goal.goalType,
                appPackageName: goal.packageName,
                title: title,
                subtitle: subtitle,
                progressLabel: progressLabel,
                progressPercent: String(format: "%.0f%%", progressFraction * 100),
                progressFraction: progressFraction,
                isExceeded: isExceeded,
                currentLimitMinutes: currentLimitMinutes,
                streak: goal.currentStreak
            )
        }
    }

    var activeGoalsCount: Int {
        state.goals.count
    }
}
