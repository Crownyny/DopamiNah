package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository

class UpdateStreakUseCase(
    private val gamificationRepository: GamificationRepository
) {
    suspend fun execute() {
        gamificationRepository.checkAndIncrementStreak()
    }
}
