import Foundation

struct UserGamificationStats {
    var level: Int
    var currentPoints: Int
    var pointsToNextLevel: Int
    var activeBadges: [String]

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
}

struct UserPremiumStatus {
    let userId: String
    var isPremium: Bool
    let activationDate: Date?
    let expiryDate: Date?
}

struct AuthUser {
    let uid: String
    let email: String?
    let displayName: String?
    let photoURL: String?

    var initials: String {
        guard let name = displayName, !name.isEmpty else {
            return email?.prefix(2).uppercased() ?? "?"
        }
        let components = name.split(separator: " ")
        if components.count >= 2 {
            return String(components[0].first ?? "A") + String(components[1].first ?? "U")
        }
        return String(name.prefix(2)).uppercased()
    }
}
