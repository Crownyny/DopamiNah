import SwiftUI
import Charts

struct StatsView: View {
    @StateObject private var viewModel = StatsViewModel()
    @State private var showDatePicker = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppSpacing.cardSpacing) {
                    StatsHeader(selectedTab: $viewModel.uiState.selectedTab)

                    StatsSummaryCards(
                        dailyAverageText: viewModel.uiState.dailyAverageText,
                        unlockAverageText: viewModel.uiState.unlockAverageText
                    )

                    StatsCarousel(
                        appUsageData: viewModel.uiState.appUsageData,
                        hourlyUsage: viewModel.uiState.hourlyUsage,
                        selectedTab: viewModel.uiState.selectedTab
                    )

                    if let details = viewModel.uiState.dailyDetails {
                        DailyDetailsCard(
                            details: details,
                            selectedDayOffset: viewModel.uiState.selectedDayOffset,
                            onPreviousDay: viewModel.goToPreviousDay,
                            onNextDay: viewModel.goToNextDay,
                            onSelectDay: { viewModel.selectDay($0) }
                        )
                    }
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .task { await viewModel.loadData() }
            .sheet(isPresented: $showDatePicker) {
                DatePickerSheet(
                    selectedDayOffset: viewModel.uiState.selectedDayOffset,
                    onSelectDay: viewModel.selectDay
                )
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showDatePicker = true }) {
                        Image(systemName: "calendar")
                            .foregroundColor(.dopaminahPurple)
                    }
                }
            }
        }
    }
}

// MARK: - Stats Header
struct StatsHeader: View {
    @Binding var selectedTab: StatsTab

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Estadísticas")
                .font(AppTypography.largeTitle())
                .foregroundColor(.textPrimary)

            Picker("Período", selection: $selectedTab) {
                ForEach(StatsTab.allCases, id: \.self) { tab in
                    Text(tab.rawValue).tag(tab)
                }
            }
            .pickerStyle(.segmented)
        }
    }
}

// MARK: - Summary Cards
struct StatsSummaryCards: View {
    let dailyAverageText: String
    let unlockAverageText: String

    var body: some View {
        HStack(spacing: 12) {
            VStack(spacing: 8) {
                Image(systemName: "clock.fill")
                    .font(.system(size: 24))
                    .foregroundColor(.dopaminahPurple)
                Text(dailyAverageText)
                    .font(AppTypography.title3())
                    .foregroundColor(.textPrimary)
                Text("Promedio diario")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }
            .frame(maxWidth: .infinity)
            .padding(20)
            .background(Color.surfaceCard)
            .cardShadow()
            .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))

            VStack(spacing: 8) {
                Image(systemName: "arrow.up.right")
                    .font(.system(size: 24))
                    .foregroundColor(.dopaminahOrange)
                Text(unlockAverageText)
                    .font(AppTypography.title3())
                    .foregroundColor(.textPrimary)
                Text("Desbloqueos")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }
            .frame(maxWidth: .infinity)
            .padding(20)
            .background(Color.surfaceCard)
            .cardShadow()
            .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
        }
    }
}

// MARK: - Charts Carousel
struct StatsCarousel: View {
    let appUsageData: [AppUsageEntry]
    let hourlyUsage: [Float]
    let selectedTab: StatsTab

    var body: some View {
        VStack(spacing: 12) {
            TabView {
                DailyUsageChartCard(usageData: [])
                    .tag(0)

                AppUsageChartCard(appUsageData: appUsageData, selectedTab: selectedTab)
                    .tag(1)

                PeakUsageChartCard(hourlyUsage: hourlyUsage, selectedTab: selectedTab)
                    .tag(2)
            }
            .tabViewStyle(.page(indexDisplayMode: .automatic))
            .frame(height: 280)
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Daily Usage Chart (Line)
struct DailyUsageChartCard: View {
    let usageData: [Float]

    var sampleData: [ChartDataPoint] {
        let days = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"]
        let values: [Float] = [180, 240, 200, 300, 260, 150, 220]
        return zip(days, values).map { ChartDataPoint(day: $0, minutes: $1) }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Uso diario")
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)

            Chart(sampleData) { point in
                LineMark(
                    x: .value("Día", point.day),
                    y: .value("Minutos", point.minutes)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [.dopaminahPurple, .dopaminahPurpleLight],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .lineStyle(StrokeStyle(lineWidth: 3))

                AreaMark(
                    x: .value("Día", point.day),
                    y: .value("Minutos", point.minutes)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [.dopaminahPurple.opacity(0.3), .dopaminahPurpleLight.opacity(0.05)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .interpolationMethod(.catmullRom)
            }
            .frame(height: 200)
            .chartYAxis {
                AxisMarks(position: .leading) { value in
                    AxisGridLine()
                    AxisValueLabel() {
                        if let minutes = value.as(Float.self) {
                            Text("\(Int(minutes))m")
                                .font(.caption)
                                .foregroundColor(.textSecondary)
                        }
                    }
                }
            }
        }
    }
}

struct ChartDataPoint: Identifiable {
    let id = UUID()
    let day: String
    let minutes: Float
}

// MARK: - App Usage Chart (Donut)
struct AppUsageChartCard: View {
    let appUsageData: [AppUsageEntry]
    let selectedTab: StatsTab

    var displayData: [AppUsageEntry] {
        appUsageData.isEmpty ? [
            AppUsageEntry(appName: "Instagram", averageHours: 1.4),
            AppUsageEntry(appName: "TikTok", averageHours: 1.0),
            AppUsageEntry(appName: "X", averageHours: 0.67),
            AppUsageEntry(appName: "WhatsApp", averageHours: 0.5),
            AppUsageEntry(appName: "Otros", averageHours: 0.33)
        ] : appUsageData
    }

    var total: Float {
        displayData.reduce(0) { $0 + $1.averageHours }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Distribución por App")
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)

            HStack {
                Chart(displayData.enumerated().map { (index, entry) in
                    AppUsageChartDataEntry(name: entry.appName, value: Double(entry.averageHours), index: index)
                }) { entry in
                    SectorMark(
                        angle: .value("Horas", entry.value),
                        innerRadius: .ratio(0.6),
                        angularInset: 2
                    )
                    .foregroundStyle(AppChartGradient.all[entry.index % AppChartGradient.all.count].start)
                }
                .frame(width: 160, height: 160)

                VStack(alignment: .leading, spacing: 8) {
                    ForEach(displayData.prefix(5)) { entry in
                        HStack(spacing: 8) {
                            Circle()
                                .fill(Color.dopaminahPurple)
                                .frame(width: 10, height: 10)
                            Text(entry.appName)
                                .font(AppTypography.caption())
                                .foregroundColor(.textPrimary)
                            Spacer()
                            Text(String(format: "%.1fh", entry.averageHours))
                                .font(AppTypography.caption())
                                .foregroundColor(.textSecondary)
                        }
                    }
                }
            }
        }
    }
}

struct AppUsageChartDataEntry: Identifiable {
    let id = UUID()
    let name: String
    let value: Double
    let index: Int
}

// MARK: - Peak Usage Chart (Bars)
struct PeakUsageChartCard: View {
    let hourlyUsage: [Float]
    let selectedTab: StatsTab

    var barData: [HourlyDataPoint] {
        let data = hourlyUsage.isEmpty ?
            [0, 0, 0, 0, 0, 0, 5, 15, 30, 10, 15, 25, 40, 20, 15, 20, 30, 45, 55, 50, 35, 20, 10, 5] :
            hourlyUsage
        return data.enumerated().map { HourlyDataPoint(hour: $0, minutes: $1) }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Horas Pico")
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)

            Chart(barData) { point in
                BarMark(
                    x: .value("Hora", String(format: "%02d", point.hour)),
                    y: .value("Minutos", point.minutes)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [.dopaminahPurple, .dopaminahPurpleLight],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                )
                .cornerRadius(4)
            }
            .frame(height: 200)
            .chartXAxis {
                AxisMarks(values: .stride(by: 4)) { value in
                    AxisGridLine()
                    AxisValueLabel() {
                        if let hour = value.as(String.self) {
                            Text(hour)
                                .font(.caption2)
                                .foregroundColor(.textSecondary)
                        }
                    }
                }
            }
            .chartYAxis {
                AxisMarks(position: .leading) { _ in
                    AxisGridLine()
                }
            }
        }
    }
}

struct HourlyDataPoint: Identifiable {
    let id = UUID()
    let hour: Int
    let minutes: Float
}

// MARK: - Daily Details Card
struct DailyDetailsCard: View {
    let details: DailyDetailStats
    let selectedDayOffset: Int
    let onPreviousDay: () -> Void
    let onNextDay: () -> Void
    let onSelectDay: (Int) -> Void

    var dayLabel: String {
        selectedDayOffset == 0 ? "Hoy" : selectedDayOffset == 1 ? "Ayer" : "Hace \(selectedDayOffset) días"
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: onPreviousDay) {
                    Image(systemName: "chevron.left")
                        .foregroundColor(selectedDayOffset < 30 ? .dopaminahPurple : .textSecondary.opacity(0.3))
                }
                .disabled(selectedDayOffset >= 30)

                Spacer()

                VStack {
                    Text(dayLabel)
                        .font(AppTypography.headline())
                        .foregroundColor(.white)
                    Text(details.dateLabel)
                        .font(AppTypography.caption())
                        .foregroundColor(.white.opacity(0.8))
                }

                Spacer()

                Button(action: onNextDay) {
                    Image(systemName: "chevron.right")
                        .foregroundColor(selectedDayOffset > 0 ? .white : .white.opacity(0.3))
                }
                .disabled(selectedDayOffset == 0)
            }
            .padding(16)
            .background(
                LinearGradient(
                    colors: [.dopaminahPurple, .dopaminahPurpleDark],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 16) {
                DetailItem(icon: "sunrise.fill", title: "Primer uso", value: details.firstUseTime)
                DetailItem(icon: "timer", title: "Sesión promedio", value: "\(details.avgSessionMinutes)m")
                DetailItem(icon: "iphone.fill", title: "Más usada", value: details.mostUsedAppName)
                DetailItem(icon: "lock.open.fill", title: "Desbloqueos", value: "\(details.unlocks)")
            }
            .padding(16)

            VStack(alignment: .leading, spacing: 8) {
                Text("Tiempo total: \(details.totalTimeMillis.formattedUsageTime)")
                    .font(AppTypography.headline())
                    .foregroundColor(.dopaminahPurple)

                Text("App más usada: \(details.mostUsedAppName) (\(details.mostUsedAppTime))")
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.dopaminahPurpleLight.opacity(0.3))
        }
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
        .overlay(
            RoundedRectangle(cornerRadius: AppSpacing.cardRadius)
                .stroke(Color.dopaminahPurpleLight, lineWidth: 1)
        )
    }
}

struct DetailItem: View {
    let icon: String
    let title: String
    let value: String

    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundColor(.dopaminahPurple)
            Text(value)
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)
            Text(title)
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)
        }
    }
}

// MARK: - Date Picker Sheet
struct DatePickerSheet: View {
    let selectedDayOffset: Int
    let onSelectDay: (Int) -> Void

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Text("Seleccionar día")
                    .font(AppTypography.title2())
                    .foregroundColor(.textPrimary)

                HStack(spacing: 12) {
                    QuickDayButton(label: "Hoy", offset: 0, selected: selectedDayOffset == 0, action: { onSelectDay(0) })
                    QuickDayButton(label: "Ayer", offset: 1, selected: selectedDayOffset == 1, action: { onSelectDay(1) })
                    QuickDayButton(label: "-3d", offset: 3, selected: selectedDayOffset == 3, action: { onSelectDay(3) })
                    QuickDayButton(label: "-7d", offset: 7, selected: selectedDayOffset == 7, action: { onSelectDay(7) })
                }

                VStack(spacing: 8) {
                    ForEach(0..<7) { i in
                        let offset = i
                        let date = Date().addingDays(-offset)
                        Button(action: { onSelectDay(offset) }) {
                            HStack {
                                Text(date.shortDayLabel)
                                    .font(AppTypography.body())
                                    .foregroundColor(.textPrimary)
                                Spacer()
                                if offset == selectedDayOffset {
                                    Image(systemName: "checkmark")
                                        .foregroundColor(.dopaminahPurple)
                                }
                            }
                            .padding(.vertical, 8)
                        }
                    }
                }
            }
            .padding(24)
            .presentationDetents([.medium])
        }
    }
}

struct QuickDayButton: View {
    let label: String
    let offset: Int
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(AppTypography.footnote())
                .foregroundColor(selected ? .white : Color.dopaminahPurple)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 10)
                .background(selected ? Color.dopaminahPurple : Color.dopaminahPurpleLight)
                .clipShape(RoundedRectangle(cornerRadius: 8))
        }
    }
}
