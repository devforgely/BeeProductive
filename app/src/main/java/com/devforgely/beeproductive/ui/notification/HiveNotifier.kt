package com.devforgely.beeproductive.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.devforgely.beeproductive.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HiveNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun morning(beeName: String) {
        show(
            id = 1,
            title = "$beeName is hovering by the hive",
            body = "A small action is enough today.",
        )
    }

    fun evening(beeName: String) {
        show(
            id = 2,
            title = "Want to tuck the day in?",
            body = "$beeName saved a quiet cell for a light review.",
        )
    }

    private fun show(id: Int, title: String, body: String) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL,
                "Hive whispers",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Gentle invitations from your hive."
            }
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_honey_drop)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .build()
        manager.notify(id, notification)
    }

    companion object {
        const val CHANNEL = "hive_whispers"
    }
}
