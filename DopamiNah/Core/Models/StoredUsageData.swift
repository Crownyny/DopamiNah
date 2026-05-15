import Foundation

struct StoredAppUsage: Codable, Identifiable, Equatable {
    var id: String { packageName }
    var packageName: String
    var appName: String
    var usageMillis: Int64
    var unlockCount: Int
}

struct StoredDailyDetail: Codable {
    var firstUseTime: String
    var avgSessionMinutes: Int
    var mostUsedAppName: String
    var mostUsedAppTime: String
}

struct StoredUsageData: Codable {
    var apps: [StoredAppUsage]
    var todayUnlocks: Int
    var yesterdayUnlocks: Int
    var hourlyUsage: [Float]
    var dailyDetail: StoredDailyDetail
    var permissionGranted: Bool

    static let `default` = StoredUsageData(
        apps: [
            StoredAppUsage(packageName: "com.instagram.android", appName: "Instagram", usageMillis: 5_040_000, unlockCount: 24),
            StoredAppUsage(packageName: "com.tiktok", appName: "TikTok", usageMillis: 3_600_000, unlockCount: 18),
            StoredAppUsage(packageName: "com.twitter.android", appName: "X", usageMillis: 2_400_000, unlockCount: 15),
            StoredAppUsage(packageName: "com.whatsapp", appName: "WhatsApp", usageMillis: 1_800_000, unlockCount: 30),
            StoredAppUsage(packageName: "com.spotify.music", appName: "Spotify", usageMillis: 1_200_000, unlockCount: 5)
        ],
        todayUnlocks: 87,
        yesterdayUnlocks: 72,
        hourlyUsage: {
            var hours: [Float] = Array(repeating: 0, count: 24)
            hours[7] = 15; hours[8] = 30; hours[9] = 10
            hours[12] = 25; hours[13] = 40; hours[14] = 20
            hours[18] = 45; hours[19] = 55; hours[20] = 50
            hours[21] = 35; hours[22] = 20; hours[23] = 10
            return hours
        }(),
        dailyDetail: StoredDailyDetail(
            firstUseTime: "7:45 AM",
            avgSessionMinutes: 12,
            mostUsedAppName: "Instagram",
            mostUsedAppTime: "1h 24m"
        ),
        permissionGranted: true
    )

    static let allAppsList: [(name: String, package: String)] = [
        ("Instagram", "com.instagram.android"),
        ("TikTok", "com.tiktok"),
        ("X", "com.twitter.android"),
        ("WhatsApp", "com.whatsapp"),
        ("Spotify", "com.spotify.music"),
        ("YouTube", "com.google.android.youtube"),
        ("Chrome", "com.android.chrome"),
        ("Maps", "com.google.android.apps.maps"),
        ("Facebook", "com.facebook.katana"),
        ("Telegram", "org.telegram.messenger"),
        ("Snapchat", "com.snapchat.android"),
        ("Netflix", "com.netflix.mediaclient"),
    ]
}
