package com.example.remainder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent?) {

        val intent:Intent= Intent(context,HomeActivity::class.java)
        p1?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val event=p1?.getStringExtra(EVENT)
        val pendingIntent:PendingIntent= PendingIntent.getActivity(context,0,intent,0)
        val builder:NotificationCompat.Builder=NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable._173002_alarm_alert_bell_internet_notice_icon)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(CONTENT_TEXT)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManagerCompat: NotificationManagerCompat= NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(123,builder.build())
    }

}