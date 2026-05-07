import SwiftUI
import SwiftData
import Combine

struct DashboardView: View {
    @StateObject private var viewModel = DashboardViewModel()
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \AppLimitGoal.id) private var goals: [AppLimitGoal]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppSpacing.cardSpacing) {
                    HeaderSection(gamificationStats: viewModel.gamificationStats)

                    if !viewModel.isLoading {
                        UsageSummaryCarousel(
                            dailyUnlocks: viewModel.dailyUnlocks,
                            yesterdayUnlocks: viewModel.yesterdayUnlocks,
                            totalDailyUsageMs: viewModel.totalDailyUsageMs,
                            appLimitCards: viewModel.appLimitCards
                        )

                        MostUsedAppsSection(
                            dailyUsageStats: viewModel.dailyUsageStats,
                            hasPermission: viewModel.hasUsagePermission
                        )
                    } else {
                        ProgressView()
                            .frame(maxWidth: .infinity, minHeight: 200)
                    }
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .task {
                await viewModel.loadData()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                viewModel.checkAndIncrementStreak()
                Task { await viewModel.refreshStats() }
            }
        }
    }
}

// MARK: - Header Section
struct HeaderSection: View {
    let gamificationStats: UserGamificationStats

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Hola de nuevo,")
                        .font(AppTypography.body())
                        .foregroundColor(.textSecondary)
                    Text("DopamiNah")
                        .font(AppTypography.largeTitle())
                        .foregroundColor(.textPrimary)
                }

                Spacer()

                HStack(spacing: 8) {
                    VStack(alignment: .trailing, spacing: 2) {
                        HStack(spacing: 4) {
                            Image(systemName: "star.fill")
                                .font(.system(size: 12))
                                .foregroundColor(.dopaminahOrange)
                            Text("\(GamificationManager.shared.totalPoints)")
                                .font(AppTypography.footnote())
                                .foregroundColor(.textPrimary)
                        }
                        Text("Nivel \(gamificationStats.level)")
                            .font(AppTypography.caption())
                            .foregroundColor(.textSecondary)
                    }

                    ZStack {
                        Circle()
                            .fill(Color.dopaminahPurpleLight)
                            .frame(width: 44, height: 44)
                        Text("⭐")
                            .font(.system(size: 20))
                    }
                }
            }

            StreakMotivationCard()
        }
    }
}

struct StreakMotivationCard: View {
    let streak = GamificationManager.shared.streak

    var body: some View {
        HStack(spacing: 12) {
            Text(streak >= 7 ? "🔥" : "🌱")
                .font(.system(size: 28))

            VStack(alignment: .leading, spacing: 4) {
                Text("Racha: \(streak) días")
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)

                let viewModel = DashboardViewModel()
                Text(viewModel.streakMotivation)
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()
        }
        .padding(16)
        .background(
            LinearGradient(
                colors: [.dopaminahPurple.opacity(0.08), .dopaminahOrange.opacity(0.05)],
                startPoint: .leading,
                endPoint: .trailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Usage Summary Carousel
struct UsageSummaryCarousel: View {
    let dailyUnlocks: Int
    let yesterdayUnlocks: Int
    let totalDailyUsageMs: Int64
    let appLimitCards: [AppLimitCardInfo]

    @State private var currentIndex = 0
    let timer = Timer.publish(every: 5, on: .main, in: .common).autoconnect()

    var cards: [AnyView] {
        var result: [AnyView] = [
            AnyView(UnlocksCard(today: dailyUnlocks, yesterday: yesterdayUnlocks)),
            AnyView(ScreenTimeCard(totalMs: totalDailyUsageMs))
        ]
        for card in appLimitCards {
            result.append(AnyView(AppLimitCardView(cardInfo: card)))
        }
        return result
    }

    var body: some View {
        VStack(spacing: 12) {
            TabView(selection: $currentIndex) {
                ForEach(Array(cards.enumerated()), id: \.offset) { index, card in
                    card
                        .tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .automatic))
            .frame(height: 140)
            .onReceive(timer) { _ in
                withAnimation {
                    currentIndex = (currentIndex + 1) % max(cards.count, 1)
                }
            }
        }
    }
}

struct UnlocksCard: View {
    let today: Int
    let yesterday: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "lock.open.fill")
                    .foregroundColor(.dopaminahPurple)
                Text("Desbloqueos")
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)
                Spacer()
            }

            Text("\(today)")
                .font(.system(.title, design: .rounded, weight: .bold))
                .foregroundColor(.dopaminahPurple)

            Text(UsageTimeUtils.calculateDiffText(today: today, yesterday: yesterday))
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct ScreenTimeCard: View {
    let totalMs: Int64

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "hourglass")
                    .foregroundColor(.dopaminahOrange)
                Text("Tiempo en Pantalla")
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)
                Spacer()
            }

            Text(totalMs.formattedUsageTime)
                .font(.system(.title, design: .rounded, weight: .bold))
                .foregroundColor(.dopaminahOrange)

            Text("Hoy")
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct AppLimitCardView: View {
    let cardInfo: AppLimitCardInfo

    var progress: Double {
        guard cardInfo.timeLimitMs > 0 else { return 0 }
        return min(Double(cardInfo.timeUsedMs) / Double(cardInfo.timeLimitMs), 1.0)
    }

    var isExceeded: Bool {
        cardInfo.timeUsedMs > cardInfo.timeLimitMs
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(cardInfo.appName)
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)
                Spacer()
                Text(cardInfo.timeUsedMs.formattedUsageTime)
                    .font(AppTypography.footnote())
                    .foregroundColor(isExceeded ? .dangerRed : .textSecondary)
            }

            ProgressView(value: progress)
                .tint(isExceeded ? .dangerRed : .dopaminahPurple)

            Text(isExceeded ? "Límite excedido!" : "Límite: \(cardInfo.timeLimitMs.formattedUsageTime)")
                .font(AppTypography.caption())
                .foregroundColor(isExceeded ? .dangerRed : .textSecondary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Most Used Apps Section
struct MostUsedAppsSection: View {
    let dailyUsageStats: [AppUsageSummary]
    let hasPermission: Bool
    @State private var searchText = ""
    @State private var sortByMostUsed = true

    var filteredApps: [AppUsageSummary] {
        var apps = dailyUsageStats
        if !searchText.isEmpty {
            apps = apps.filter { $0.appName.localizedCaseInsensitiveContains(searchText) }
        }
        apps.sort {
            sortByMostUsed ?
                $0.totalTimeForegroundMillis > $1.totalTimeForegroundMillis :
                $0.totalTimeForegroundMillis < $1.totalTimeForegroundMillis
        }
        return apps
    }

    var totalUsage: Int64 {
        dailyUsageStats.reduce(0) { $0 + $1.totalTimeForegroundMillis }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Apps más usadas")
                    .font(AppTypography.title3())
                    .foregroundColor(.textPrimary)
                Spacer()

                Picker("Ordenar", selection: $sortByMostUsed) {
                    Text("Más usadas").tag(true)
                    Text("Menos usadas").tag(false)
                }
                .pickerStyle(.segmented)
                .frame(width: 150)
            }

            if !hasPermission {
                VStack(spacing: 12) {
                    Image(systemName: "lock.shield.fill")
                        .font(.system(size: 40))
                        .foregroundColor(.textSecondary)
                    Text("Sin permiso de uso")
                        .font(AppTypography.headline())
                        .foregroundColor(.textSecondary)
                    Text("Activa el permiso de Tiempo en Pantalla en Ajustes")
                        .font(AppTypography.caption())
                        .foregroundColor(.textSecondary)
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity)
                .padding(32)
                .background(Color.surfaceCard)
                .cardShadow()
                .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
            } else {
                LazyVStack(spacing: 12) {
                    ForEach(Array(filteredApps.prefix(5).enumerated()), id: \.element.id) { index, usage in
                        AppUsageItem(usage: usage, totalUsage: totalUsage)
                    }
                }
            }
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct AppUsageItem: View {
    let usage: AppUsageSummary
    let totalUsage: Int64

    var progress: Double {
        guard totalUsage > 0 else { return 0 }
        return Double(usage.totalTimeForegroundMillis) / Double(totalUsage)
    }

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: 40, height: 40)
                Image(systemName: "app.fill")
                    .font(.system(size: 18))
                    .foregroundColor(.dopaminahPurple)
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(usage.appName)
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)

                ProgressView(value: progress)
                    .tint(.dopaminahPurple)

                Text(usage.totalTimeForegroundMillis.formattedUsageTime)
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()

            Text(usage.totalTimeForegroundMillis.formattedUsageTime)
                .font(AppTypography.footnote())
                .foregroundColor(.textSecondary)
                .padding(.horizontal, 10)
                .padding(.vertical, 4)
                .background(Color.textSecondary.opacity(0.1))
                .clipShape(Capsule())
        }
        .padding(.vertical, 4)
    }
}
