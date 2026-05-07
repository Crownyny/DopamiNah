import SwiftUI
import SwiftData

struct GoalsView: View {
    @StateObject private var viewModel = GoalsViewModel()
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \AppLimitGoal.id) private var goals: [AppLimitGoal]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppSpacing.cardSpacing) {
                    GoalsHeader(activeGoalsCount: viewModel.activeGoalsCount)

                    if viewModel.state.isLoading {
                        ProgressView()
                            .frame(maxWidth: .infinity, minHeight: 200)
                    } else if goals.isEmpty {
                        EmptyGoalsView(onAddGoal: viewModel.showCreateGoalDialog)
                    } else {
                        LazyVStack(spacing: 12) {
                            ForEach(goals, id: \.id) { goal in
                                GoalCardView(
                                    goal: goal,
                                    onDelete: { Task { await viewModel.deleteGoal(id: goal.id) } },
                                    onEdit: { newLimit in Task { await viewModel.editGoal(id: goal.id, newLimitMinutes: newLimit) } }
                                )
                            }
                        }
                    }

                    AddGoalButton(onClick: viewModel.showCreateGoalDialog)

                    GoalsTipCard()
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .task { await viewModel.loadData() }
            .sheet(isPresented: $viewModel.state.showCreateDialog) {
                CreateGoalDialog(
                    installedApps: viewModel.state.installedApps,
                    onSave: { type, app, limit in
                        Task { await viewModel.submitNewGoal(typeLabel: type, appName: app, limitMinutes: limit) }
                    }
                )
            }
        }
    }
}

// MARK: - Goals Header
struct GoalsHeader: View {
    let activeGoalsCount: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Tus reglas, tu ritmo.")
                .font(AppTypography.largeTitle())
                .foregroundColor(.textPrimary)

            Text("\(activeGoalsCount) metas activas")
                .font(AppTypography.body())
                .foregroundColor(.textSecondary)
        }
    }
}

// MARK: - Goal Card
struct GoalCardView: View {
    let goal: AppLimitGoal
    let onDelete: () -> Void
    let onEdit: (Int) -> Void
    @State private var showEditDialog = false
    @State private var showDeleteConfirmation = false

    var progress: Double {
        switch goal.goalType {
        case GoalType.appLimit, GoalType.totalDaily:
            guard goal.maxTimeMillis > 0 else { return 0 }
            return 0.5
        case GoalType.unlockLimit:
            guard goal.maxUnlocks > 0 else { return 0 }
            return 0.5
        default:
            return 0
        }
    }

    var isExceeded: Bool { progress > 0.8 }

    var title: String {
        switch goal.goalType {
        case GoalType.appLimit: return goal.appDisplayName
        case GoalType.totalDaily: return "Tiempo Total Diario"
        case GoalType.unlockLimit: return "Límite de Desbloqueos"
        default: return goal.appDisplayName
        }
    }

    var subtitle: String {
        switch goal.goalType {
        case GoalType.appLimit: return "Límite de \(Int(goal.maxTimeMillis / 60_000))m"
        case GoalType.totalDaily: return "Límite de \(Int(goal.maxTimeMillis / 60_000))m"
        case GoalType.unlockLimit: return "Máximo \(goal.maxUnlocks)"
        default: return ""
        }
    }

    var progressLabel: String {
        switch goal.goalType {
        case GoalType.appLimit: return "0m / \(Int(goal.maxTimeMillis / 60_000))m"
        case GoalType.totalDaily: return "0m / \(Int(goal.maxTimeMillis / 60_000))m"
        case GoalType.unlockLimit: return "0 / \(goal.maxUnlocks)"
        default: return ""
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                goalIcon

                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(AppTypography.headline())
                        .foregroundColor(.textPrimary)
                    Text(subtitle)
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                }

                Spacer()

                HStack(spacing: 4) {
                    if goal.currentStreak > 0 {
                        Text("\(goal.currentStreak)d 🔥")
                            .font(AppTypography.caption())
                            .foregroundColor(.dopaminahOrange)
                    }

                    Menu {
                        Button("Editar") { showEditDialog = true }
                        Button("Eliminar", role: .destructive) { showDeleteConfirmation = true }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                            .foregroundColor(.textSecondary)
                    }
                }
            }

            ProgressView(value: progress)
                .tint(isExceeded ? .dangerRed : .dopaminahOrange)

            HStack {
                Text(progressLabel)
                    .font(AppTypography.caption())
                    .foregroundColor(isExceeded ? .dangerRed : .textSecondary)

                if isExceeded {
                    Spacer()
                    HStack(spacing: 4) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.caption)
                            .foregroundColor(.dangerRed)
                        Text("Límite excedido")
                            .font(AppTypography.caption())
                            .foregroundColor(.dangerRed)
                    }
                }
            }
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
        .sheet(isPresented: $showEditDialog) {
            EditGoalDialog(
                currentLimitMinutes: goal.goalType == GoalType.unlockLimit ?
                    goal.maxUnlocks : Int(goal.maxTimeMillis / 60_000),
                goalTitle: title,
                isUnlockType: goal.goalType == GoalType.unlockLimit,
                onSave: onEdit
            )
        }
        .alert("Eliminar meta", isPresented: $showDeleteConfirmation) {
            Button("Cancelar", role: .cancel) {}
            Button("Eliminar", role: .destructive, action: onDelete)
        } message: {
            Text("¿Estás seguro de que quieres eliminar esta meta?")
        }
    }

    var goalIcon: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 10)
                .fill(isExceeded ? Color.dangerRed.opacity(0.15) : Color.dopaminahPurpleLight)
                .frame(width: 44, height: 44)

            Image(systemName: goalTypeIcon)
                .font(.system(size: 20))
                .foregroundColor(isExceeded ? .dangerRed : .dopaminahPurple)
        }
    }

    var goalTypeIcon: String {
        switch goal.goalType {
        case GoalType.appLimit: return "app.fill"
        case GoalType.totalDaily: return "hourglass"
        case GoalType.unlockLimit: return "lock.open.fill"
        default: return "target"
        }
    }
}

// MARK: - Add Goal Button
struct AddGoalButton: View {
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 8) {
                Image(systemName: "plus.circle.fill")
                    .font(.system(size: 20))
                Text("Nueva Meta")
                    .font(AppTypography.headline())
            }
            .foregroundColor(.dopaminahPurple)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(Color.dopaminahPurpleLight)
            .clipShape(RoundedRectangle(cornerRadius: AppSpacing.buttonRadius))
        }
    }
}

// MARK: - Goals Tip Card
struct GoalsTipCard: View {
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "lightbulb.fill")
                .font(.system(size: 24))
                .foregroundColor(.dopaminahOrange)

            VStack(alignment: .leading, spacing: 4) {
                Text("Consejo")
                    .font(AppTypography.headline())
                    .foregroundColor(.dopaminahOrange)
                Text("Establece límites realistas. Es mejor empezar con metas pequeñas e ir incrementando.")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }
        }
        .padding(16)
        .background(Color.dopaminahOrange.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Empty Goals View
struct EmptyGoalsView: View {
    let onAddGoal: () -> Void

    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "target")
                .font(.system(size: 60))
                .foregroundColor(.dopaminahPurpleLight)

            Text("Sin metas aún")
                .font(AppTypography.title2())
                .foregroundColor(.textPrimary)

            Text("Crea tu primera meta para comenzar a controlar tu tiempo en pantalla.")
                .font(AppTypography.body())
                .foregroundColor(.textSecondary)
                .multilineTextAlignment(.center)

            Button(action: onAddGoal) {
                Text("Crear primera meta")
                    .font(AppTypography.headline())
                    .foregroundColor(.white)
                    .padding(.horizontal, 32)
                    .padding(.vertical, 14)
                    .background(Color.dopaminahPurple)
                    .clipShape(Capsule())
            }
        }
        .padding(40)
        .frame(maxWidth: .infinity)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Create Goal Dialog
struct CreateGoalDialog: View {
    let installedApps: [String]
    let onSave: (String, String, Int) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var selectedType = GoalType.totalDaily
    @State private var selectedApp = ""
    @State private var limitText = ""

    var limitMinutes: Int? {
        Int(limitText)
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Tipo de Meta") {
                    Picker("Tipo", selection: $selectedType) {
                        Text("Tiempo Total Diario").tag(GoalType.totalDaily)
                        Text("App Específica").tag(GoalType.appLimit)
                        Text("Límite de Desbloqueos").tag(GoalType.unlockLimit)
                    }
                }

                if selectedType == GoalType.appLimit {
                    Section("Aplicación") {
                        Picker("App", selection: $selectedApp) {
                            Text("Seleccionar...").tag("")
                            ForEach(installedApps, id: \.self) { app in
                                Text(app).tag(app)
                            }
                        }
                    }
                }

                Section("Límite") {
                    TextField(
                        selectedType == GoalType.unlockLimit ? "Número máximo" : "Minutos",
                        text: $limitText
                    )
                    .keyboardType(.numberPad)
                }
            }
            .navigationTitle("Nueva Meta")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Guardar") {
                        guard let limit = limitMinutes, limit > 0 else { return }
                        if selectedType == GoalType.appLimit && selectedApp.isEmpty { return }
                        onSave(selectedType, selectedApp, limit)
                        dismiss()
                    }
                    .disabled(limitText.isEmpty)
                }
            }
        }
        .presentationDetents([.medium])
    }
}

// MARK: - Edit Goal Dialog
struct EditGoalDialog: View {
    let currentLimitMinutes: Int
    let goalTitle: String
    let isUnlockType: Bool
    let onSave: (Int) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var limitText = ""

    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text(goalTitle)) {
                    TextField(
                        isUnlockType ? "Nuevo máximo de desbloqueos" : "Nuevo límite en minutos",
                        text: $limitText
                    )
                    .keyboardType(.numberPad)

                    Text("El cambio tendrá efecto desde mañana")
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                }
            }
            .navigationTitle("Editar Meta")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Guardar") {
                        guard let limit = Int(limitText), limit > 0 else { return }
                        onSave(limit)
                        dismiss()
                    }
                    .disabled(limitText.isEmpty)
                }
            }
            .onAppear {
                limitText = "\(currentLimitMinutes)"
            }
        }
    }
}
