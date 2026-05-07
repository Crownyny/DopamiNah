import SwiftUI

// MARK: - Brand Colors
extension Color {
    static let dopaminahPurple = Color(hex: "8B5CF6")
    static let dopaminahPurpleDark = Color(hex: "6D28D9")
    static let dopaminahPurpleLight = Color(hex: "DDD6FE")
    static let dopaminahOrange = Color(hex: "FA832B")

    // MARK: - Semantic Colors
    static let successGreen = Color(hex: "22C55E")
    static let warningYellow = Color(hex: "EAB308")
    static let dangerRed = Color(hex: "EF4444")

    // MARK: - Light Mode
    static let backgroundLight = Color(hex: "F8FAFC")
    static let surfaceCard = Color(hex: "FFFFFF")
    static let textPrimary = Color(hex: "0F172A")
    static let textSecondary = Color(hex: "64748B")

    // MARK: - Dark Mode
    static let backgroundDark = Color(hex: "1C1B1F")
    static let surfaceDark = Color(hex: "2B2930")
    static let textPrimaryDark = Color(hex: "E6E1E5")
    static let textSecondaryDark = Color(hex: "CAC4D0")
}

// MARK: - Chart Gradient Colors
struct AppChartGradient: Identifiable {
    let id = UUID()
    let start: Color
    let end: Color

    static let all: [AppChartGradient] = [
        AppChartGradient(start: Color(hex: "8B5CF6"), end: Color(hex: "A78BFA")),
        AppChartGradient(start: Color(hex: "F43F5E"), end: Color(hex: "FB7185")),
        AppChartGradient(start: Color(hex: "3B82F6"), end: Color(hex: "60A5FA")),
        AppChartGradient(start: Color(hex: "10B981"), end: Color(hex: "6EE7B7")),
        AppChartGradient(start: Color(hex: "F59E0B"), end: Color(hex: "FCD34D")),
        AppChartGradient(start: Color(hex: "6366F1"), end: Color(hex: "A5B4FC")),
        AppChartGradient(start: Color(hex: "EC4899"), end: Color(hex: "F9A8D4")),
        AppChartGradient(start: Color(hex: "14B8A6"), end: Color(hex: "5EEAD4"))
    ]
}

// MARK: - Background/Surface resolver
struct ThemeColors {
    static var background: Color {
        #if os(iOS)
        return Color("BackgroundLight", bundle: nil)
        #else
        return .backgroundLight
        #endif
    }

    static var surface: Color {
        .surfaceCard
    }

    static var textPrimary: Color {
        .textPrimary
    }

    static var textSecondary: Color {
        .textSecondary
    }
}
