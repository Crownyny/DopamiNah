import Foundation
import SwiftUI
import Combine

struct AchievementsState {
    var streakDays: Int = 7
    var level: Int = 5
    var badges: [BadgeUi] = [
        BadgeUi(emoji: "🌅", title: "Early Bird", description: "Abre la app antes de las 8 AM por 5 días", unlockDate: "2026-04-20"),
        BadgeUi(emoji: "🎯", title: "Focus Master", description: "Cumple todas tus metas por 7 días seguidos", unlockDate: "2026-04-25"),
        BadgeUi(emoji: "💪", title: "Autodisciplina", description: "Usa menos de 1 hora diaria por 3 días", unlockDate: nil),
        BadgeUi(emoji: "🧘", title: "Zen Digital", description: "Mantén una racha de 30 días", unlockDate: nil),
        BadgeUi(emoji: "🏆", title: "Leyenda", description: "Alcanza el nivel 10", unlockDate: nil),
        BadgeUi(emoji: "⭐", title: "Constancia", description: "Abre la app todos los días por un mes", unlockDate: nil)
    ]
    var nextBadgeEmoji: String = "💪"
    var nextBadgeTitle: String = "Autodisciplina"
    var nextBadgeDescription: String = "Usa menos de 1 hora diaria por 3 días consecutivos"
    var unlockedCount: Int = 2
    var totalCount: Int = 6
    var bestStreak: Int = 7
}

@MainActor
final class AchievementsViewModel: ObservableObject {
    @Published var state = AchievementsState()

    func loadData() {
        let gamification = GamificationManager.shared
        state.streakDays = gamification.streak
        state.level = gamification.stats.level
        state.bestStreak = max(gamification.bestStreak, gamification.streak)

        state.unlockedCount = state.badges.filter(\.isUnlocked).count
        state.totalCount = state.badges.count
    }
}

struct AchievementsView: View {
    @StateObject private var viewModel = AchievementsViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppSpacing.cardSpacing) {
                    AchievementsHeaderView()

                    StreakCard(streakDays: viewModel.state.streakDays)

                    LevelCard(level: viewModel.state.level, streakDays: viewModel.state.streakDays)

                    BadgesGrid(badges: viewModel.state.badges)

                    NextAchievementCard(
                        emoji: viewModel.state.nextBadgeEmoji,
                        title: viewModel.state.nextBadgeTitle,
                        description: viewModel.state.nextBadgeDescription
                    )

                    RewardsSystemCard()

                    AchievementStatsCard(
                        unlockedCount: viewModel.state.unlockedCount,
                        totalCount: viewModel.state.totalCount,
                        level: viewModel.state.level,
                        bestStreak: viewModel.state.bestStreak
                    )
                }
                .padding(.horizontal, AppSpacing.horizontalPadding)
                .padding(.top, AppSpacing.topPadding)
                .padding(.bottom, AppSpacing.bottomPadding)
            }
            .background(Color.backgroundLight.ignoresSafeArea())
            .onAppear { viewModel.loadData() }
        }
    }
}

// MARK: - Achievements Header
struct AchievementsHeaderView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Logros")
                .font(AppTypography.largeTitle())
                .foregroundColor(.textPrimary)

            HStack(spacing: 8) {
                Image(systemName: "rosette")
                    .font(.system(size: 20))
                    .foregroundColor(.dopaminahOrange)
                Text("Tu progreso en la ruta del bienestar digital")
                    .font(AppTypography.body())
                    .foregroundColor(.textSecondary)
            }
        }
    }
}

// MARK: - Streak Card
struct StreakCard: View {
    let streakDays: Int

    var body: some View {
        VStack(spacing: 16) {
            Text("🔥")
                .font(.system(size: 64))

            Text("\(streakDays)")
                .font(.system(.title, design: .rounded, weight: .bold))
                .foregroundColor(.dopaminahOrange)

            Text("días de racha")
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)

            if streakDays >= 30 {
                Text("LEGENDARIO")
                    .font(AppTypography.footnote())
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 6)
                    .background(
                        LinearGradient(
                            colors: [.dopaminahOrange, .dangerRed],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .clipShape(Capsule())
            }
        }
        .frame(maxWidth: .infinity)
        .padding(24)
        .background(
            LinearGradient(
                colors: [.dopaminahOrange.opacity(0.15), .dopaminahPurple.opacity(0.05)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Level Card
struct LevelCard: View {
    let level: Int
    let streakDays: Int

    var levelTitle: String {
        switch level {
        case 1: return "Aprendiz Digital"
        case 2: return "Explorador Consciente"
        case 3: return "Guerrero del Enfoque"
        case 4: return "Sabio del Tiempo"
        case 5: return "Maestro Zen"
        default: return "Leyenda de la Dopamina"
        }
    }

    var progress: Double {
        Double(streakDays) / Double(level * 10)
    }

    var body: some View {
        VStack(spacing: 12) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Nivel \(level)")
                        .font(AppTypography.title3())
                        .foregroundColor(.textPrimary)
                    Text(levelTitle)
                        .font(AppTypography.footnote())
                        .foregroundColor(.dopaminahPurple)
                }

                Spacer()

                HStack(spacing: 4) {
                    Image(systemName: "flame.fill")
                        .font(.system(size: 14))
                        .foregroundColor(.dopaminahOrange)
                    Text("\(streakDays)d")
                        .font(AppTypography.footnote())
                        .foregroundColor(.textSecondary)
                }
            }

            ProgressView(value: min(progress, 1.0))
                .tint(.dopaminahPurple)

            Text("Faltan \(max(0, level * 10 - streakDays)) días para el nivel \(level + 1)")
                .font(AppTypography.caption())
                .foregroundColor(.textSecondary)
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Badges Grid
struct BadgesGrid: View {
    let badges: [BadgeUi]
    let columns = [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Insignias")
                .font(AppTypography.title3())
                .foregroundColor(.textPrimary)

            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(badges) { badge in
                    BadgeCard(badge: badge)
                }
            }
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct BadgeCard: View {
    let badge: BadgeUi

    var body: some View {
        VStack(spacing: 8) {
            ZStack {
                if badge.isUnlocked {
                    LinearGradient(colors: [.dopaminahPurple, .dopaminahPurpleDark], startPoint: .topLeading, endPoint: .bottomTrailing)
                        .clipShape(Circle())
                        .frame(width: 56, height: 56)
                } else {
                    Color.textSecondary.opacity(0.15)
                        .clipShape(Circle())
                        .frame(width: 56, height: 56)
                }

                if badge.isUnlocked {
                    Text(badge.emoji)
                        .font(.system(size: 28))
                } else {
                    Image(systemName: "lock.fill")
                        .font(.system(size: 20))
                        .foregroundColor(.textSecondary.opacity(0.5))
                }
            }

            Text(badge.title)
                .font(AppTypography.caption())
                .foregroundColor(.textPrimary)
                .multilineTextAlignment(.center)
                .lineLimit(1)
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Next Achievement Card
struct NextAchievementCard: View {
    let emoji: String
    let title: String
    let description: String

    var body: some View {
        HStack(spacing: 16) {
            Text(emoji)
                .font(.system(size: 40))

            VStack(alignment: .leading, spacing: 4) {
                Text("Próximo logro")
                    .font(AppTypography.caption())
                    .foregroundColor(.dopaminahPurple)
                Text(title)
                    .font(AppTypography.headline())
                    .foregroundColor(.textPrimary)
                Text(description)
                    .font(AppTypography.caption())
                    .foregroundColor(.textSecondary)
            }

            Spacer()

            Image(systemName: "arrow.right.circle.fill")
                .font(.system(size: 24))
                .foregroundColor(.dopaminahPurpleLight)
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

// MARK: - Rewards System Card
struct RewardsSystemCard: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Sistema de Recompensas")
                .font(AppTypography.title3())
                .foregroundColor(.textPrimary)

            VStack(spacing: 8) {
                RewardItem(icon: "arrow.up.circle.fill", text: "+10 puntos por abrir la app cada día")
                RewardItem(icon: "fire.fill", text: "+50 puntos diarios por cumplir todas las metas")
                RewardItem(icon: "star.circle.fill", text: "Sube de nivel cada 500 puntos acumulados")
            }
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct RewardItem: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.dopaminahPurple)
            Text(text)
                .font(AppTypography.body())
                .foregroundColor(.textSecondary)
            Spacer()
        }
    }
}

// MARK: - Achievement Stats Card
struct AchievementStatsCard: View {
    let unlockedCount: Int
    let totalCount: Int
    let level: Int
    let bestStreak: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Estadísticas")
                .font(AppTypography.title3())
                .foregroundColor(.textPrimary)

            VStack(spacing: 0) {
                StatRow(label: "Insignias desbloqueadas", value: "\(unlockedCount) / \(totalCount)")
                Divider()
                StatRow(label: "Nivel actual", value: "\(level)")
                Divider()
                StatRow(label: "Mejor racha", value: "\(bestStreak) días")
            }
        }
        .padding(16)
        .background(Color.surfaceCard)
        .cardShadow()
        .clipShape(RoundedRectangle(cornerRadius: AppSpacing.cardRadius))
    }
}

struct StatRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(AppTypography.body())
                .foregroundColor(.textSecondary)
            Spacer()
            Text(value)
                .font(AppTypography.headline())
                .foregroundColor(.textPrimary)
        }
        .padding(.vertical, 8)
    }
}
