import SwiftUI

// MARK: - iTunes API

private struct ITunesResult: Codable {
    let artworkUrl100: String?
    let artworkUrl512: String?
}

private struct ITunesResponse: Codable {
    let results: [ITunesResult]
}

private func searchArtworkURL(for appName: String) async throws -> URL {
    let term = appName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? appName
    let url = URL(string: "https://itunes.apple.com/search?term=\(term)&entity=software&limit=1&country=us")!
    let (data, _) = try await URLSession.shared.data(from: url)
    let response = try JSONDecoder().decode(ITunesResponse.self, from: data)
    guard let result = response.results.first else {
        throw URLError(.cannotFindHost)
    }
    let urlString = result.artworkUrl512 ?? result.artworkUrl100 ?? ""
    guard let artworkURL = URL(string: urlString) else {
        throw URLError(.badURL)
    }
    return artworkURL
}

// MARK: - Cache

@MainActor
final class AppIconCache {
    static let shared = AppIconCache()

    private var memory: [String: UIImage] = [:]
    private let fm = FileManager.default

    private var cacheDir: URL {
        let dir = fm.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("AppIcons", isDirectory: true)
        if !fm.fileExists(atPath: dir.path) {
            try? fm.createDirectory(at: dir, withIntermediateDirectories: true)
        }
        return dir
    }

    func get(_ key: String) -> UIImage? {
        let safe = key.lowercased().safeFilename
        if let img = memory[safe] { return img }
        let url = cacheDir.appendingPathComponent("\(safe).png")
        guard let data = try? Data(contentsOf: url), let img = UIImage(data: data) else { return nil }
        memory[safe] = img
        return img
    }

    func set(_ key: String, _ image: UIImage) {
        let safe = key.lowercased().safeFilename
        memory[safe] = image
        let url = cacheDir.appendingPathComponent("\(safe).png")
        try? image.pngData()?.write(to: url)
    }
}

// MARK: - Download

private func downloadAppIcon(appName: String) async throws -> UIImage {
    let artworkURL = try await searchArtworkURL(for: appName)
    let (data, _) = try await URLSession.shared.data(from: artworkURL)
    guard let image = UIImage(data: data) else {
        throw URLError(.cannotDecodeContentData)
    }
    return image
}

// MARK: - View

struct AppIconView: View {
    let appName: String
    let size: CGFloat

    @State private var image: UIImage?
    @State private var isLoading = true

    var body: some View {
        ZStack {
            if let img = image {
                Image(uiImage: img)
                    .resizable()
                    .scaledToFit()
                    .frame(width: size, height: size)
                    .clipShape(RoundedRectangle(cornerRadius: size * 0.22))
            } else if isLoading {
                RoundedRectangle(cornerRadius: size * 0.22)
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: size, height: size)
                    .overlay(ProgressView().tint(.dopaminahPurple))
            } else {
                RoundedRectangle(cornerRadius: size * 0.22)
                    .fill(Color.dopaminahPurpleLight)
                    .frame(width: size, height: size)
                    .overlay(
                        Image(systemName: AppIconMapper.iconName(forAppName: appName))
                            .font(.system(size: size * 0.45))
                            .foregroundColor(.dopaminahPurple)
                    )
            }
        }
        .task { await load() }
    }

    private func load() async {
        let cacheKey = appName.lowercased()
        if let cached = AppIconCache.shared.get(cacheKey) {
            image = cached
            isLoading = false
            return
        }
        do {
            let img = try await downloadAppIcon(appName: appName)
            AppIconCache.shared.set(cacheKey, img)
            image = img
        } catch {
            image = nil
        }
        isLoading = false
    }
}
