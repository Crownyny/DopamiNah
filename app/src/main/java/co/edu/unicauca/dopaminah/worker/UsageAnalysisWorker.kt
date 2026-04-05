package co.edu.unicauca.dopaminah.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.edu.unicauca.dopaminah.domain.usecase.CheckUsageLimitsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UsageAnalysisWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkUsageLimitsUseCase: CheckUsageLimitsUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            checkUsageLimitsUseCase.execute()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
