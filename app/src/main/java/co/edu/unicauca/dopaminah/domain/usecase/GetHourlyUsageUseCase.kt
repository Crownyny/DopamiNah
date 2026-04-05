package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import javax.inject.Inject

class GetHourlyUsageUseCase @Inject constructor(
    private val deviceUsageRepository: DeviceUsageRepository
) {
    suspend fun execute(days: Int): List<Float> {
        // This UseCase could handle additional logic like normalization or 
        // special day-by-day aggregations that shouldn't live in the Repository.
        return deviceUsageRepository.getHourlyUsage(days)
    }
}
