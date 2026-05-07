import SwiftUI

// MARK: - Typography
struct AppTypography {
    static func largeTitle() -> Font {
        .system(.largeTitle, design: .rounded, weight: .bold)
    }

    static func title2() -> Font {
        .system(.title2, design: .rounded, weight: .semibold)
    }

    static func title3() -> Font {
        .system(.title3, design: .rounded, weight: .semibold)
    }

    static func body() -> Font {
        .system(.body, design: .rounded, weight: .regular)
    }

    static func footnote() -> Font {
        .system(.footnote, design: .rounded, weight: .medium)
    }

    static func caption() -> Font {
        .system(.caption, design: .rounded, weight: .medium)
    }

    static func headline() -> Font {
        .system(.headline, design: .rounded, weight: .semibold)
    }
}

// MARK: - Spacing
struct AppSpacing {
    static let horizontalPadding: CGFloat = 16
    static let topPadding: CGFloat = 16
    static let bottomPadding: CGFloat = 24
    static let bottomNavPadding: CGFloat = 80
    static let cardSpacing: CGFloat = 16
    static let cardRadius: CGFloat = 16
    static let buttonRadius: CGFloat = 12
}

// MARK: - Shadow
struct AppShadow {
    static let card = ShadowStyle(
        color: Color.black.opacity(0.05),
        radius: 8,
        x: 0,
        y: 4
    )
}

struct ShadowStyle {
    let color: Color
    let radius: CGFloat
    let x: CGFloat
    let y: CGFloat
}

extension View {
    func cardShadow() -> some View {
        self.shadow(
            color: AppShadow.card.color,
            radius: AppShadow.card.radius,
            x: AppShadow.card.x,
            y: AppShadow.card.y
        )
    }
}
