package com.devforgely.beeproductive.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.devforgely.beeproductive.ui.notification.HiveNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: UserPreferencesRepository,
) {
    suspend fun applySchedule() {
        val prefs = preferences.prefs.first()
        val work = WorkManager.getInstance(context)
        if (prefs.morningNotes) {
            enqueueDaily(MORNING_WORK, HiveNotificationWorker.KIND_MORNING, hour = 8)
        } else {
            work.cancelUniqueWork(MORNING_WORK)
        }
        if (prefs.eveningNotes) {
            enqueueDaily(EVENING_WORK, HiveNotificationWorker.KIND_EVENING, hour = 20)
        } else {
            work.cancelUniqueWork(EVENING_WORK)
        }
        work.cancelUniqueWork(TRIAL_WORK)
    }

    private fun enqueueDaily(name: String, kind: String, hour: Int) {
        val request = PeriodicWorkRequestBuilder<HiveNotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayUntil(hour), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(HiveNotificationWorker.KIND to kind))
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            name,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    private fun delayUntil(hour: Int): Long {
        val now = ZonedDateTime.now()
        var next = now.withHour(hour).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis()
    }

    companion object {
        private const val MORNING_WORK = "hive-morning"
        private const val EVENING_WORK = "hive-evening"
        private const val TRIAL_WORK = "hive-trial"
    }
}
