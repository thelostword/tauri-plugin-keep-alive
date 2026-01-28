package com.plugin.keep_alive

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class KeepAliveService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "keep_alive_channel"
        private const val CHANNEL_NAME = "Keep Alive Service"
        private var isServiceRunning = false

        fun isRunning(): Boolean {
            return isServiceRunning
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var notificationTitle = "应用运行中"
    private var notificationMessage = "正在后台同步数据..."

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 读取传入的参数
        intent?.let {
            it.getStringExtra("title")?.let { title -> notificationTitle = title }
            it.getStringExtra("message")?.let { msg -> notificationMessage = msg }
        }

        startForegroundService()
        acquireWakeLock()

        return START_STICKY // 服务被杀死后会自动重启
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        releaseWakeLock()
    }

    /**
     * 创建通知渠道（Android 8.0+需要）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keep the app alive in background"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                // 禁止用户关闭通知
                setAllowBubbles(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 启动前台服务
     */
    private fun startForegroundService() {
        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 使通知不可滑动删除
            .setAutoCancel(false) // 禁止自动取消
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        // Android 10 (API 29) 及以上需要指定前台服务类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    /**
     * 获取唤醒锁，防止CPU休眠
     */
    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "KeepAlive::WakeLock"
            ).apply {
                // 添加超时保护，10小时后自动释放（推荐做法）
                acquire(10 * 60 * 60 * 1000L) // 10 hours in milliseconds
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 释放唤醒锁
     */
    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            wakeLock = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // 任务被移除时重启服务和应用
        restartServiceAndApp()
    }

    /**
     * 重启服务和应用
     */
    private fun restartServiceAndApp() {
        // 1. 重启服务
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        restartServiceIntent.putExtra("title", notificationTitle)
        restartServiceIntent.putExtra("message", notificationMessage)

        val restartServicePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getService(
                applicationContext,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(
                applicationContext,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        // 2. 重启应用
        val restartAppIntent = packageManager.getLaunchIntentForPackage(packageName)
        restartAppIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val restartAppPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                applicationContext,
                2,
                restartAppIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                2,
                restartAppIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 先重启服务（1秒后）
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            restartServicePendingIntent
        )

        // 再重启应用（2秒后）
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 2000,
            restartAppPendingIntent
        )
    }
}
