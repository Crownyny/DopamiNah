import SwiftUI

struct DebugDataView: View {
    @StateObject private var viewModel = DebugDataViewModel()
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            Form {
                resetMessageSection
                appsSection
                unlocksSection
                distributionSection
                dailyDetailSection
                actionSection
            }
            .navigationTitle("Datos de Prueba")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancelar") { dismiss() }
                }
            }
            .alert("Añadir app", isPresented: $viewModel.showAddApp) {
                AddAppAlert { name, pkg, mins, unlocks in
                    viewModel.addApp(name: name, package: pkg, usageMinutes: mins, unlocks: unlocks)
                }
            }
            .alert("Restaurar datos", isPresented: $viewModel.showResetAlert) {
                Button("Cancelar", role: .cancel) {}
                Button("Restaurar", role: .destructive) {
                    viewModel.resetToDefaults()
                }
            } message: {
                Text("Se perderán todos los datos manuales. ¿Continuar?")
            }
        }
    }

    @ViewBuilder private var resetMessageSection: some View {
        if let msg = viewModel.resetMessage {
            Section {
                Text(msg)
                    .font(AppTypography.caption())
                    .foregroundColor(.successGreen)
            }
        }
    }

    private var appsSection: some View {
        Section("Aplicaciones") {
            ForEach(viewModel.apps) { app in
                NavigationLink(destination: EditAppView(
                    appName: app.appName,
                    packageName: app.packageName,
                    usageMinutes: Int(app.usageMillis / 60_000),
                    unlocks: app.unlockCount,
                    onSave: { name, pkg, mins, unlocks in
                        if let idx = viewModel.apps.firstIndex(where: { $0.packageName == app.packageName }) {
                            viewModel.apps[idx] = StoredAppUsage(
                                packageName: pkg,
                                appName: name,
                                usageMillis: Int64(mins) * 60_000,
                                unlockCount: unlocks
                            )
                        }
                    }
                )) {
                    HStack {
                        Text(app.appName)
                            .font(AppTypography.body())
                            .foregroundColor(.textPrimary)
                        Spacer()
                        Text(app.usageMillis.formattedUsageTime)
                            .font(AppTypography.caption())
                            .foregroundColor(.textSecondary)
                    }
                }
            }
            .onDelete(perform: viewModel.deleteApp)

            Button(action: { viewModel.showAddApp = true }) {
                Label("Añadir app", systemImage: "plus.circle.fill")
                    .font(AppTypography.body())
                    .foregroundColor(.dopaminahPurple)
            }
        }
    }

    private var unlocksSection: some View {
        Section("Desbloqueos") {
            HStack {
                Text("Hoy")
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Spacer()
                TextField("0", text: $viewModel.todayUnlocks)
                    .keyboardType(.numberPad)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 80)
            }
            HStack {
                Text("Ayer")
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Spacer()
                TextField("0", text: $viewModel.yesterdayUnlocks)
                    .keyboardType(.numberPad)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 80)
            }
        }
    }

    private var distributionSection: some View {
        Section("Distribución horaria") {
            Picker("Patrón", selection: $viewModel.selectedHourlyPreset) {
                ForEach(DebugDataViewModel.HourlyPreset.allCases, id: \.self) { preset in
                    Text(preset.rawValue).tag(preset)
                }
            }
            .pickerStyle(.segmented)

            let peak = viewModel.selectedHourlyPreset.values
                .enumerated()
                .max(by: { $0.element < $1.element })
            if let peak {
                Text("Pico de uso a las \(peak.offset):00 — \(Int(peak.element)) min")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }
        }
    }

    private var dailyDetailSection: some View {
        Section("Detalle del día") {
            HStack {
                Text("Primer uso")
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Spacer()
                TextField("7:45 AM", text: $viewModel.firstUseTime)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 120)
            }
            HStack {
                Text("Sesión promedio")
                    .font(AppTypography.body())
                    .foregroundColor(.textPrimary)
                Spacer()
                HStack {
                    TextField("12", text: $viewModel.avgSessionMinutes)
                        .keyboardType(.numberPad)
                        .multilineTextAlignment(.trailing)
                        .frame(width: 50)
                    Text("min")
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                }
            }
        }
    }

    private var actionSection: some View {
        Section {
            Button(action: {
                viewModel.save()
                dismiss()
            }) {
                HStack {
                    Spacer()
                    Text("Guardar cambios")
                        .font(AppTypography.headline())
                        .foregroundColor(.white)
                    Spacer()
                }
            }
            .listRowBackground(Color.dopaminahPurple)

            Button(role: .destructive) {
                viewModel.showResetAlert = true
            } label: {
                HStack {
                    Spacer()
                    Text("Restaurar datos por defecto")
                        .font(AppTypography.body())
                    Spacer()
                }
            }
        }
    }
}

// MARK: - Edit App View
struct EditAppView: View {
    @State var appName: String
    @State var packageName: String
    @State var usageMinutes: Int
    @State var unlocks: Int
    let onSave: (String, String, Int, Int) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var usageText: String = ""
    @State private var unlocksText: String = ""

    var body: some View {
        Form {
            Section("Aplicación") {
                HStack {
                    Text("Nombre")
                        .font(AppTypography.body())
                        .foregroundColor(.textSecondary)
                    Spacer()
                    Text(appName)
                        .font(AppTypography.body())
                        .foregroundColor(.textPrimary)
                }
                HStack {
                    Text("Paquete")
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                    Spacer()
                    Text(packageName)
                        .font(AppTypography.caption())
                        .foregroundColor(.textPrimary)
                }
            }

            Section("Uso") {
                HStack {
                    Text("Tiempo (min)")
                        .font(AppTypography.body())
                        .foregroundColor(.textPrimary)
                    Spacer()
                    TextField("\(usageMinutes)", text: $usageText)
                        .keyboardType(.numberPad)
                        .multilineTextAlignment(.trailing)
                        .frame(width: 80)
                }
                HStack {
                    Text("Desbloqueos")
                        .font(AppTypography.body())
                        .foregroundColor(.textPrimary)
                    Spacer()
                    TextField("\(unlocks)", text: $unlocksText)
                        .keyboardType(.numberPad)
                        .multilineTextAlignment(.trailing)
                        .frame(width: 80)
                }
            }

            Section {
                Button(action: {
                    onSave(
                        appName,
                        packageName,
                        Int(usageText).flatMap { $0 > 0 ? $0 : usageMinutes } ?? usageMinutes,
                        Int(unlocksText).flatMap { $0 >= 0 ? $0 : unlocks } ?? unlocks
                    )
                    dismiss()
                }) {
                    HStack {
                        Spacer()
                        Text("Guardar")
                            .font(AppTypography.headline())
                            .foregroundColor(.white)
                        Spacer()
                    }
                }
                .listRowBackground(Color.dopaminahPurple)
            }
        }
        .navigationTitle("Editar App")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            usageText = "\(usageMinutes)"
            unlocksText = "\(unlocks)"
        }
    }
}

// MARK: - Add App Alert
struct AddAppAlert: View {
    let onSave: (String, String, Int, Int) -> Void
    @State private var selectedApp = ""
    @State private var usageMinutes = ""
    @State private var unlocks = ""

    var body: some View {
        Group {
            TextField("Nombre de la app", text: $selectedApp)
            TextField("Minutos de uso", text: $usageMinutes)
                .keyboardType(.numberPad)
            TextField("Desbloqueos", text: $unlocks)
                .keyboardType(.numberPad)
            Button("Cancelar", role: .cancel) {}
            Button("Añadir") {
                let name = selectedApp.trimmingCharacters(in: .whitespaces)
                guard !name.isEmpty else { return }
                onSave(
                    name,
                    name.lowercased().replacingOccurrences(of: " ", with: "."),
                    Int(usageMinutes) ?? 30,
                    Int(unlocks) ?? 5
                )
            }
        }
    }
}
