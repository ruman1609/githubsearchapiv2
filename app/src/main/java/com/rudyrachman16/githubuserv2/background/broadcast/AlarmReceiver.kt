package com.rudyrachman16.githubuserv2.background.broadcast

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.rudyrachman16.githubuserv2.MainActivity
import com.rudyrachman16.githubuserv2.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val REQ_CODE = 100  // same with notifID
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = context.getString(R.string.notif_title)
        val msg = context.getString(R.string.notif_msg)
        setNotification(context, title, msg)
    }

    private fun setNotification(context: Context, title: String, msg: String) {
        val channelID = "channel01"
        val channelName = "Reminder Search"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundURI =
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.chinese_drum}".toUri()
        val vibration = longArrayOf(1000, 500, 1000, 600, 1000)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent =
            PendingIntent.getActivity(context, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelID).apply {
            setSmallIcon(R.drawable.github_logo)
            setContentTitle(title)
            setContentText(msg)
            setContentIntent(pendingIntent)
            setVibrate(vibration)
            setAutoCancel(true)
            setSound(soundURI)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        channelID,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        val atr = AudioAttributes.Builder().apply {
                            setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        }.build()
                        setSound(soundURI, atr)
                        enableLights(true)
                        enableVibration(true)
                        vibrationPattern = vibration
                        lightColor = R.color.white
                    }
                setChannelId(channelID)
                manager.createNotificationChannel(channel)
            }
        }.build()
        manager.notify(REQ_CODE, notification)
    }

    fun setAlarm(context: Context) {
        val time = "09:00".split(":").toTypedArray()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time[0].toInt())
            set(Calendar.MINUTE, time[1].toInt())
        }
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, REQ_CODE, intent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, context.getString(R.string.reminder_on), Toast.LENGTH_SHORT).show()
    }

    fun isAlarmSet(context: Context): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQ_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    fun cancelAlarm(context: Context) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, REQ_CODE, intent, 0)
        pendingIntent.cancel()
        manager.cancel(pendingIntent)
        Toast.makeText(context, context.getString(R.string.reminder_off), Toast.LENGTH_SHORT).show()
    }
}