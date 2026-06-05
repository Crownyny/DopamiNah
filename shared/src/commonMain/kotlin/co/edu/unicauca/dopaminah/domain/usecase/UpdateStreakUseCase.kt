package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository

/** Use case that checks and increments the user's daily streak via the gamification repository. */
class UpdateStreakUseCase(
    private val gamificationRepository: GamificationRepository
) {
    suspend fun execute() {
        gamificationRepository.checkAndIncrementStreak()
    }
}
