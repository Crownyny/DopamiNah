import Foundation
import SwiftData

actor SwiftDataGoalsRepository: GoalsRepositoryProtocol {
    private let modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    func getAllGoals() async -> [AppLimitGoal] {
        let fetchDescriptor = FetchDescriptor<AppLimitGoal>()
        do {
            return try modelContext.fetch(fetchDescriptor)
        } catch {
            print("Error fetching goals: \(error)")
            return []
        }
    }

    func saveGoal(_ goal: AppLimitGoal) async {
        modelContext.insert(goal)
        try? modelContext.save()
    }

    func deleteGoal(id: UUID) async {
        let fetchDescriptor = FetchDescriptor<AppLimitGoal>(
            predicate: #Predicate { $0.id == id }
        )
        do {
            let goals = try modelContext.fetch(fetchDescriptor)
            if let goal = goals.first {
                modelContext.delete(goal)
                try? modelContext.save()
            }
        } catch {
            print("Error deleting goal: \(error)")
        }
    }

    func updateGoal(_ goal: AppLimitGoal) async {
        try? modelContext.save()
    }
}
