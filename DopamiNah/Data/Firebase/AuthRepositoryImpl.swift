import Foundation
import FirebaseCore
import FirebaseAuth
import AuthenticationServices
import GoogleSignIn
import Combine

@MainActor
final class AuthRepositoryImpl: AuthRepositoryProtocol, ObservableObject {
    @Published var user: AuthUser?

    private var currentUserStream: AsyncStream<AuthUser?>?
    private var currentUserContinuation: AsyncStream<AuthUser?>.Continuation?

    var currentUserPublisher: AsyncStream<AuthUser?> {
        if currentUserStream == nil {
            currentUserStream = AsyncStream { continuation in
                self.currentUserContinuation = continuation
            }
        }
        return currentUserStream!
    }

    init() {
        if let firebaseUser = Auth.auth().currentUser {
            user = AuthUser(
                uid: firebaseUser.uid,
                email: firebaseUser.email,
                displayName: firebaseUser.displayName,
                photoURL: firebaseUser.photoURL?.absoluteString
            )
        }
    }

    var currentUser: AuthUser? {
        get async {
            if let firebaseUser = Auth.auth().currentUser {
                return AuthUser(
                    uid: firebaseUser.uid,
                    email: firebaseUser.email,
                    displayName: firebaseUser.displayName,
                    photoURL: firebaseUser.photoURL?.absoluteString
                )
            }
            return nil
        }
    }

    func getCurrentUser() -> AuthUser? {
        guard let firebaseUser = Auth.auth().currentUser else { return nil }
        return AuthUser(
            uid: firebaseUser.uid,
            email: firebaseUser.email,
            displayName: firebaseUser.displayName,
            photoURL: firebaseUser.photoURL?.absoluteString
        )
    }

    func signInWithGoogle(presenting: UIViewController) async throws -> AuthUser {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            throw AuthError.noClientID
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config

        return try await withCheckedThrowingContinuation { continuation in
            GIDSignIn.sharedInstance.signIn(
                withPresenting: presenting
            ) { signInResult, error in
                if let error {
                    continuation.resume(throwing: error)
                    return
                }

                guard let result = signInResult,
                      let idToken = result.user.idToken?.tokenString else {
                    continuation.resume(throwing: AuthError.noToken)
                    return
                }

                let credential = GoogleAuthProvider.credential(
                    withIDToken: idToken,
                    accessToken: result.user.accessToken.tokenString
                )

                Task {
                    do {
                        let authResult = try await Auth.auth().signIn(with: credential)
                        let firebaseUser = authResult.user
                        let authUser = AuthUser(
                            uid: firebaseUser.uid,
                            email: firebaseUser.email,
                            displayName: firebaseUser.displayName,
                            photoURL: firebaseUser.photoURL?.absoluteString
                        )
                        self.user = authUser
                        self.currentUserContinuation?.yield(authUser)
                        continuation.resume(returning: authUser)
                    } catch {
                        continuation.resume(throwing: error)
                    }
                }
            }
        }
    }

    func signInWithApple(presenting: UIViewController) async throws -> AuthUser {
        return try await withCheckedThrowingContinuation { continuation in
            let appleIDProvider = ASAuthorizationAppleIDProvider()
            let request = appleIDProvider.createRequest()
            request.requestedScopes = [.fullName, .email]

            let authorizationController = ASAuthorizationController(authorizationRequests: [request])
            let delegate = AppleSignInDelegate { result in
                switch result {
                case .success(let credential):
                    guard let appleIDCredential = credential as? ASAuthorizationAppleIDCredential,
                          let identityToken = appleIDCredential.identityToken,
                          let idTokenString = String(data: identityToken, encoding: .utf8) else {
                        continuation.resume(throwing: AuthError.noToken)
                        return
                    }

                    let firebaseCredential = OAuthProvider.appleCredential(
                        withIDToken: idTokenString,
                        rawNonce: "",
                        fullName: appleIDCredential.fullName
                    )

                    Task {
                        do {
                            let authResult = try await Auth.auth().signIn(with: firebaseCredential)
                            let firebaseUser = authResult.user
                            let authUser = AuthUser(
                                uid: firebaseUser.uid,
                                email: firebaseUser.email,
                                displayName: firebaseUser.displayName ?? appleIDCredential.fullName?.givenName,
                                photoURL: nil
                            )
                            self.user = authUser
                            self.currentUserContinuation?.yield(authUser)
                            continuation.resume(returning: authUser)
                        } catch {
                            continuation.resume(throwing: error)
                        }
                    }

                case .failure(let error):
                    continuation.resume(throwing: error)
                }
            }

            authorizationController.delegate = delegate
            authorizationController.presentationContextProvider = presenting as? ASAuthorizationControllerPresentationContextProviding
            authorizationController.performRequests()
        }
    }

    func signOut() async {
        try? Auth.auth().signOut()
        GIDSignIn.sharedInstance.signOut()
        user = nil
        currentUserContinuation?.yield(nil)
    }
}

enum AuthError: Error, LocalizedError {
    case noClientID
    case noToken
    case cancelled

    var errorDescription: String? {
        switch self {
        case .noClientID: return "No Firebase client ID configured"
        case .noToken: return "No authentication token received"
        case .cancelled: return "Sign-in was cancelled"
        }
    }
}

extension ASAuthorizationControllerPresentationContextProviding {
}

