import Foundation

struct UsageTimeUtils {
    static func formatUsageTime(timeInMillis: Int64) -> String {
        let totalMinutes = timeInMillis / 60_000
        let hours = totalMinutes / 60
        let minutes = totalMinutes % 60

        if hours > 0 {
            return "\(hours)h \(minutes)m"
        } else {
            return "\(minutes)m"
        }
    }

    static func calculateDiffText(today: Int, yesterday: Int) -> String {
        if today > yesterday {
            return "Up +\(today - yesterday) vs ayer"
        } else if today < yesterday {
            return "Down -\(yesterday - today) vs ayer"
        } else {
            return "= Igual que ayer"
        }
    }

    static func calculateTimeDiff(today: Int64, yesterday: Int64) -> String {
        let diff = today - yesterday
        if diff > 0 {
            return "Up +\(formatUsageTime(timeInMillis: diff)) vs ayer"
        } else if diff < 0 {
            return "Down -\(formatUsageTime(timeInMillis: abs(diff))) vs ayer"
        } else {
            return "= Igual que ayer"
        }
    }

    static func formatTimeOfDay(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "h:mm a"
        formatter.locale = Locale(identifier: "es_ES")
        return formatter.string(from: date)
    }
}
