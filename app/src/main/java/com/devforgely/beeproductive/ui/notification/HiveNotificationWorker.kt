package com.devforgely.beeproductive.ui.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devforgely.beeproductive.data.local.dao.HiveDao
import com.devforgely.beeproductive.data.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class HiveNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notifier: HiveNotifier,
    private val dao: HiveDao,
    private val preferences: UserPreferencesRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val prefs = preferences.prefs.first()
        val name = dao.bee()?.name ?: "Your bee"
        when (inputData.getString(KIND)) {
            KIND_MORNING -> if (prefs.morningNotes) notifier.morning(name)
            KIND_EVENING -> if (prefs.eveningNotes) notifier.evening(name)
        }
        return Result.success()
    }

    companion object {
        const val KIND = "kind"
        const val KIND_MORNING = "morning"
        const val KIND_EVENING = "evening"
    }
}
