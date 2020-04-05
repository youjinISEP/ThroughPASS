package com.world.magicline

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.world.magicline.obj.Prop.FCM_MSG_CODE
import com.world.magicline.obj.Prop.TAG
import com.world.magicline.obj.Prop.fcmTokenId


/*
 * FCM 메세징 클래스
 * 이해원
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
        override fun onNewToken(token: String) {
            super.onNewToken(token)

            // Get new Instance ID token
            // ID 초기 생성(앱 삭제 후 설치), 재성성 시 호출됨
            fcmTokenId = token
            Log.d(TAG, "Refreshed Token : $token");
        }

        // 메세지 수신 시 호출되는 함수
        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Check if message contains a data payload.
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data payload: " + remoteMessage.data);
                sendNotification(remoteMessage.data["body"]!!, remoteMessage.data["title"]!!)
//                if (/* Check if data needs to be processed by long running job */ true) {
//                    // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                } else {
//                    // Handle message within 10 seconds
//                    handleNow();
//                }
            }

        }

    @SuppressLint("InvalidWakeLockTag")
    private fun sendNotification(body: String, title: String) {

        // 화면 꺼진 상태에서 화면 키게 하기
//        var powerManager : PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager;
//        var wakeLock: PowerManager.WakeLock = powerManager.run {
//            newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WAKELOCK").apply {
//                acquire(3000)
//            }
//        }
//        wakeLock.release()

//        var wakeLock: PowerManager.WakeLock = powerManager.newWakeLock(
//                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");

        Log.d(TAG, "알림 메세지 : $body , 그리고 제목 : $title")

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent : PendingIntent = PendingIntent.getActivity(this, FCM_MSG_CODE, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "Default Channel ID"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setLights(Color.BLUE, 1, 1)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "대기 잔여 시간 알림"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(2000, 200, 2000, 200, 500)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(FCM_MSG_CODE, notificationBuilder.build())
    }


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }
}