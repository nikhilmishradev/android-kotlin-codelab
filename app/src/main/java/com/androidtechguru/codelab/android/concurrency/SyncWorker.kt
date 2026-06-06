package com.androidtechguru.codelab.android.concurrency

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * CONCURRENCY — WorkManager Worker
 *
 * Key concepts:
 * 1. CoroutineWorker — suspend-based worker (recommended)
 * 2. InputData / OutputData — pass data to/from worker
 * 3. Result — success, retry, failure
 * 4. Constraints — network, battery, charging, storage
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Read input data
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()

        return try {
            // Simulate sync work
            setProgress(workDataOf("progress" to 0))
            delay(1000)
            setProgress(workDataOf("progress" to 50))
            delay(1000)
            setProgress(workDataOf("progress" to 100))

            // Return output data
            val outputData = workDataOf(
                KEY_SYNC_COUNT to 42,
                KEY_LAST_SYNC to System.currentTimeMillis()
            )
            Result.success(outputData)

        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()  // will be retried with exponential backoff
            } else {
                Result.failure(workDataOf("error" to e.message))
            }
        }
    }

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_SYNC_COUNT = "sync_count"
        const val KEY_LAST_SYNC = "last_sync"
        const val MAX_RETRIES = 3
        const val UNIQUE_WORK_NAME = "sync_work"
    }
}

// ── WorkManager Helper — Enqueue and observe work ──
object WorkManagerHelper {

    // ── One-time work request ──
    fun createSyncRequest(userId: String): OneTimeWorkRequest {
        val inputData = workDataOf(SyncWorker.KEY_USER_ID to userId)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)  // needs internet
            .setRequiresBatteryNotLow(true)                 // don't run on low battery
            .build()

        return OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,  // 10s, 20s, 40s...
                10, TimeUnit.SECONDS
            )
            .addTag("sync")
            .build()
    }

    // ── Periodic work request ──
    fun createPeriodicSync(): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 1, TimeUnit.HOURS,
            flexInterval = 15, TimeUnit.MINUTES  // can run within last 15 min of interval
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
    }

    // ── Chained work ──
    fun enqueueChainedWork(workManager: WorkManager, userId: String) {
        val syncWork = createSyncRequest(userId)
        val cleanupWork = OneTimeWorkRequestBuilder<SyncWorker>() // different worker in real app
            .addTag("cleanup")
            .build()

        // Sequential chain: sync THEN cleanup
        workManager
            .beginUniqueWork(
                SyncWorker.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,  // cancel existing if running
                syncWork
            )
            .then(cleanupWork)  // runs after syncWork succeeds
            .enqueue()
    }

    // ── Observe work ──
    // In ViewModel:
    // val workInfo: Flow<WorkInfo?> = workManager
    //     .getWorkInfoByIdFlow(request.id)
    //     .map { it }
    //
    // In Compose:
    // val workInfo by workManager.getWorkInfoByIdLiveData(id)
    //     .observeAsState()
    // when (workInfo?.state) {
    //     WorkInfo.State.RUNNING -> showProgress()
    //     WorkInfo.State.SUCCEEDED -> showSuccess(workInfo?.outputData)
    //     WorkInfo.State.FAILED -> showError()
    // }
}

// INTERVIEW TIP — When to use WorkManager:
//
// WorkManager: guaranteed execution, survives process death, respects constraints
//   Use for: sync, upload, backup, periodic cleanup
//
// Coroutines (viewModelScope): tied to component lifecycle
//   Use for: API calls, DB queries, UI-driven async work
//
// Foreground Service: long-running, user-visible work
//   Use for: music playback, GPS tracking, ongoing downloads
