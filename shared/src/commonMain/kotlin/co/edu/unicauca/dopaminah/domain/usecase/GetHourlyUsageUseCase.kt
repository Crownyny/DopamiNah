package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository

class GetHourlyUsageUseCase(
    private val deviceUsageRepository: DeviceUsageRepository
) {
    suspend fun execute(days: Int): List<Float> {
        return deviceUsageRepository.getHourlyUsage(days)
    }
}
