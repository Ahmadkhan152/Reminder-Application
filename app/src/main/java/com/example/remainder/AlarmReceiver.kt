package com.example.remainder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent?) {

        val shifter:Intent= Intent(p0,RecyclerviewActivity::class.java)
        p1?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent:PendingIntent= PendingIntent.getActivity(p0,0,shifter,0)
        val builder:NotificationCompat.Builder=NotificationCompat.Builder(p0,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(CONTENT_TEXT)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManegerCompat: NotificationManagerCompat= NotificationManagerCompat.from(p0)
        notificationManegerCompat.notify(123,builder.build())
    }

}