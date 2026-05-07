import Foundation
import FirebaseDatabase
import Combine

@MainActor
final class PremiumRepositoryImpl: PremiumRepositoryProtocol, ObservableObject {
    @Published var premiumStatus: UserPremiumStatus?

    private var handle: DatabaseHandle?
    private let database: Database

    init(database: Database = Database.database()) {
        self.database = database
    }

    func getPremiumStatus(userId: String) async -> UserPremiumStatus {
        let ref = database.reference().child("premiumUsers").child(userId)

        return await withCheckedContinuation { continuation in
            handle = ref.observe(.value) { [weak self] snapshot in
                guard let value = snapshot.value as? [String: Any] else {
                    let status = UserPremiumStatus(
                        userId: userId,
                        isPremium: false,
                        activationDate: nil,
                        expiryDate: nil
                    )
                    self?.premiumStatus = status
                    continuation.resume(returning: status)
                    return
                }

                let isPremium = value["isPremium"] as? Bool ?? false
                let activationDate = (value["activationDate"] as? TimeInterval).map { Date(timeIntervalSince1970: $0) }
                let expiryDate = (value["expiryDate"] as? TimeInterval).map { Date(timeIntervalSince1970: $0) }

                let status = UserPremiumStatus(
                    userId: userId,
                    isPremium: isPremium,
                    activationDate: activationDate,
                    expiryDate: expiryDate
                )
                self?.premiumStatus = status
                continuation.resume(returning: status)
            }
        }
    }

    func setPremiumStatus(userId: String, isPremium: Bool) async throws {
        let ref = database.reference().child("premiumUsers").child(userId)
        let data: [String: Any] = [
            "isPremium": isPremium,
            "activationDate": Date().timeIntervalSince1970,
            "expiryDate": isPremium ? Date(timeIntervalSinceNow: 365 * 24 * 3600).timeIntervalSince1970 : 0
        ]

        try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
            ref.updateChildValues(data) { error, _ in
                if let error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(returning: ())
                }
            }
        }
    }

    func isPremiumUser(userId: String) async -> Bool {
        let ref = database.reference().child("premiumUsers").child(userId).child("isPremium")

        return await withCheckedContinuation { continuation in
            ref.getData { error, snapshot in
                if let isPremium = snapshot?.value as? Bool {
                    continuation.resume(returning: isPremium)
                } else {
                    continuation.resume(returning: false)
                }
            }
        }
    }

    func removeListener() {
        if let handle {
            database.reference().removeObserver(withHandle: handle)
        }
    }
}
