import Foundation

struct AppIconMapper {
    static func iconName(for packageName: String) -> String {
        let pkg = packageName.lowercased()
        if pkg.contains("instagram") { return "camera.fill" }
        if pkg.contains("tiktok") { return "music.note.tv.fill" }
        if pkg.contains("twitter") || pkg.contains("x.com") { return "message.fill" }
        if pkg.contains("whatsapp") { return "message.bubble.fill" }
        if pkg.contains("spotify") { return "music.note.list" }
        if pkg.contains("youtube") { return "play.rectangle.fill" }
        if pkg.contains("chrome") || pkg.contains("browser") { return "globe" }
        if pkg.contains("maps") || pkg.contains("map") { return "map.fill" }
        if pkg.contains("facebook") { return "person.2.fill" }
        if pkg.contains("telegram") { return "paperplane.fill" }
        if pkg.contains("snapchat") { return "camera.macro" }
        if pkg.contains("netflix") { return "play.tv.fill" }
        if pkg.contains("twitch") { return "gamecontroller.fill" }
        if pkg.contains("discord") { return "bubble.left.and.bubble.right.fill" }
        if pkg.contains("reddit") { return "text.bubble.fill" }
        if pkg.contains("linkedin") { return "suitcase.fill" }
        if pkg.contains("pinterest") { return "pin.fill" }
        if pkg.contains("gmail") || pkg.contains("mail") { return "envelope.fill" }
        if pkg.contains("calendar") { return "calendar" }
        if pkg.contains("photos") || pkg.contains("gallery") { return "photo.fill" }
        if pkg.contains("notes") { return "note.text" }
        if pkg.contains("clock") || pkg.contains("alarm") { return "clock.fill" }
        if pkg.contains("weather") { return "cloud.sun.fill" }
        if pkg.contains("health") || pkg.contains("fitness") { return "heart.fill" }
        if pkg.contains("wallet") || pkg.contains("pay") { return "wallet.pass.fill" }
        if pkg.contains("phone") || pkg.contains("dialer") { return "phone.fill" }
        if pkg.contains("messages") || pkg.contains("sms") { return "sms.fill" }
        if pkg.contains("settings") || pkg.contains("preferences") { return "gearshape.fill" }
        return "app.fill"
    }

    static func iconName(forAppName appName: String) -> String {
        let name = appName.lowercased()
        if name == "instagram" { return "camera.fill" }
        if name == "tiktok" { return "music.note.tv.fill" }
        if name == "x" || name.contains("twitter") { return "message.fill" }
        if name.contains("whatsapp") { return "message.bubble.fill" }
        if name == "spotify" { return "music.note.list" }
        if name.contains("youtube") { return "play.rectangle.fill" }
        if name == "chrome" { return "globe" }
        if name == "maps" || name == "google maps" || name == "apple maps" { return "map.fill" }
        if name.contains("facebook") { return "person.2.fill" }
        if name == "telegram" { return "paperplane.fill" }
        if name.contains("snapchat") { return "camera.macro" }
        if name.contains("netflix") { return "play.tv.fill" }
        if name == "twitch" { return "gamecontroller.fill" }
        if name == "discord" { return "bubble.left.and.bubble.right.fill" }
        if name == "reddit" { return "text.bubble.fill" }
        if name.contains("linkedin") { return "suitcase.fill" }
        if name.contains("pinterest") { return "pin.fill" }
        if name.contains("gmail") || name.contains("mail") { return "envelope.fill" }
        if name == "photos" { return "photo.fill" }
        if name == "clock" { return "clock.fill" }
        if name == "weather" { return "cloud.sun.fill" }
        if name == "health" { return "heart.fill" }
        if name == "settings" { return "gearshape.fill" }
        return iconName(for: "") 
    }
}
