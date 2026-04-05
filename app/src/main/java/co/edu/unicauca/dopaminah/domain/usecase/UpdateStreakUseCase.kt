package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import javax.inject.Inject

class UpdateStreakUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    suspend fun execute() {
        // Business logic for streak updates could be more complex here in the future
        // For now, it delegates to the repository but we've established the layer.
        gamificationRepository.incrementStreakAndPoints()
    }
}
