import Foundation

@MainActor
final class DeviceUsageRepositoryMock: DeviceUsageRepositoryProtocol {
    private let permissionGranted: Bool

    init(permissionGranted: Bool = true) {
        self.permissionGranted = permissionGranted
    }

    func hasUsageStatsPermission() async -> Bool {
        permissionGranted
    }

    func getDailyUsageStats() async -> [AppUsageSummary] {
        [
            AppUsageSummary(packageName: "com.instagram.android", appName: "Instagram", totalTimeForegroundMillis: 5_040_000, unlockCount: 24, lastTimeUsed: Date().addingTimeInterval(-300)),
            AppUsageSummary(packageName: "com.tiktok", appName: "TikTok", totalTimeForegroundMillis: 3_600_000, unlockCount: 18, lastTimeUsed: Date().addingTimeInterval(-600)),
            AppUsageSummary(packageName: "com.twitter.android", appName: "X", totalTimeForegroundMillis: 2_400_000, unlockCount: 15, lastTimeUsed: Date().addingTimeInterval(-900)),
            AppUsageSummary(packageName: "com.whatsapp", appName: "WhatsApp", totalTimeForegroundMillis: 1_800_000, unlockCount: 30, lastTimeUsed: Date().addingTimeInterval(-1200)),
            AppUsageSummary(packageName: "com.spotify.music", appName: "Spotify", totalTimeForegroundMillis: 1_200_000, unlockCount: 5, lastTimeUsed: Date().addingTimeInterval(-1800))
        ]
    }

    func getDailyDeviceUnlocks() async -> Int {
        87
    }

    func getYesterdayDeviceUnlocks() async -> Int {
        72
    }

    func getAverageUsageMillis(days: Int) async -> Int64 {
        Int64(days) * 3_600_000
    }

    func getAverageUnlocks(days: Int) async -> Int {
        80
    }

    func getDailyUsageForLastDays(days: Int) async -> [Int64] {
        (0..<days).map { _ in Int64.random(in: 2_000_000...6_000_000) }
    }

    func getAverageUsagePerApp(days: Int, limit: Int) async -> [(String, Int64)] {
        [
            ("Instagram", 5_040_000),
            ("TikTok", 3_600_000),
            ("X", 2_400_000),
            ("WhatsApp", 1_800_000),
            ("Spotify", 1_200_000),
            ("YouTube", 900_000),
            ("Chrome", 600_000),
            ("Maps", 300_000)
        ].prefix(limit).map { ($0, $1) }
    }

    func getDailyDetails(dayOffset: Int) async -> DailyDetailStats {
        let date = Date().addingDays(-dayOffset)
        return DailyDetailStats(
            dateLabel: date.dayLabel,
            firstUseTime: "7:45 AM",
            avgSessionMinutes: 12,
            mostUsedAppName: "Instagram",
            mostUsedAppTime: "1h 24m",
            unlocks: 87,
            totalTimeMillis: 14_040_000
        )
    }

    func getHourlyUsage(days: Int) async -> [Float] {
        var hours: [Float] = Array(repeating: 0, count: 24)
        hours[7] = 15
        hours[8] = 30
        hours[9] = 10
        hours[12] = 25
        hours[13] = 40
        hours[14] = 20
        hours[18] = 45
        hours[19] = 55
        hours[20] = 50
        hours[21] = 35
        hours[22] = 20
        hours[23] = 10
        return hours
    }

    func checkUsageLimits() async -> [AppLimitCardInfo] {
        [
            AppLimitCardInfo(packageName: "com.instagram.android", appName: "Instagram", timeUsedMs: 5_040_000, timeLimitMs: 3_600_000),
            AppLimitCardInfo(packageName: "com.tiktok", appName: "TikTok", timeUsedMs: 3_600_000, timeLimitMs: 3_600_000),
        ]
    }
}
