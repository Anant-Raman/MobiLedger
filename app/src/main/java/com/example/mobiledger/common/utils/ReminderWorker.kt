package com.example.mobiledger.common.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mobiledger.R
import com.example.mobiledger.presentation.main.MainActivity


class ReminderWorker(val context: Context, val workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        createNotification(ConstantUtils.APP_NAME, ConstantUtils.REMINDER_MESSAGE)
        return Result.success()
    }

    private fun createNotification(title: String, description: String) {

        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationID = (0..1000000).random()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    ConstantUtils.CHANNEL_ID_REMINDER,
                    ConstantUtils.REMINDER_CHANNEL,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, ConstantUtils.CHANNEL_ID_REMINDER)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(resultPendingIntent)

        notificationManager.notify(notificationID, notificationBuilder.build())

    }
}