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

    // MARK: - Adaptive Colors
    static var backgroundLight: Color {
        Color(uiColor: UIColor { $0.userInterfaceStyle == .dark
            ? UIColor(red: 28/255, green: 27/255, blue: 31/255, alpha: 1)
            : UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)
        })
    }

    static var surfaceCard: Color {
        Color(uiColor: UIColor { $0.userInterfaceStyle == .dark
            ? UIColor(red: 43/255, green: 41/255, blue: 48/255, alpha: 1)
            : UIColor(red: 255/255, green: 255/255, blue: 255/255, alpha: 1)
        })
    }

    static var textPrimary: Color {
        Color(uiColor: UIColor { $0.userInterfaceStyle == .dark
            ? UIColor(red: 230/255, green: 225/255, blue: 229/255, alpha: 1)
            : UIColor(red: 15/255, green: 23/255, blue: 42/255, alpha: 1)
        })
    }

    static var textSecondary: Color {
        Color(uiColor: UIColor { $0.userInterfaceStyle == .dark
            ? UIColor(red: 202/255, green: 196/255, blue: 208/255, alpha: 1)
            : UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        })
    }
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

// MARK: - Background/Surface resolver (legacy)
struct ThemeColors {
    static var background: Color {
        .backgroundLight
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
