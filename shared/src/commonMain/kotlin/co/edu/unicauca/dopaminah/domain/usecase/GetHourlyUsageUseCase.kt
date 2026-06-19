package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository

/** Use case that retrieves hourly usage data for a given number of past days. */
class GetHourlyUsageUseCase(
    private val deviceUsageRepository: DeviceUsageRepository
) {
    suspend fun execute(days: Int): List<Float> {
        return deviceUsageRepository.getHourlyUsage(days)
    }
}
