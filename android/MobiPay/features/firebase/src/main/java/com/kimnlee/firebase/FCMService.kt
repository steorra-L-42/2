package com.kimnlee.firebase

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.Person
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.BackendService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "FCMService"

class FCMService : FirebaseMessagingService() {

    private lateinit var mNotificationManager: NotificationManagerCompat

    private val fcmApi = ApiClient.getInstance().fcmApi
    private val backendService = fcmApi.create(BackendService::class.java)

    fun getToken(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                callback(token)
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                callback("Failed")
            }
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: FCM onCreate")


        // FCM 토큰 확인
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                sendTokenToServer(token) // 서버에 전송
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
            }
        }

        Log.d(TAG, "onCreate: BASE URL = ${fcmApi.baseUrl()}")

        mNotificationManager = NotificationManagerCompat.from(applicationContext)
    }

    fun createReplyRemoteInput(context: Context): RemoteInput {
        return RemoteInput.Builder(REMOTE_INPUT_RESULT_KEY).build()
    }

    fun createReplyIntent(
        context: Context, appConversation: MobiConversation): Intent {

        val intent = Intent(context, MessagingService::class.java)

        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)

        return intent
    }


    fun createReplyAction(
        context: Context, appConversation: MobiConversation): NotificationCompat.Action {
        val replyIntent: Intent = createReplyIntent(context, appConversation)

        val replyPendingIntent = PendingIntent.getService(
            context,
            155,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_mobipay, "Reply", replyPendingIntent)

            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)

            .setShowsUserInterface(false)

            .addRemoteInput(createReplyRemoteInput(context))
            .build()

        return replyAction
    }

    fun createMarkAsReadIntent(
        context: Context, appConversation: MobiConversation): Intent {
        val intent = Intent(context, MessagingService::class.java)
        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)
        return intent
    }

    fun createMarkAsReadAction(
        context: Context, appConversation: MobiConversation): NotificationCompat.Action {
        val markAsReadIntent = createMarkAsReadIntent(context, appConversation)
        val markAsReadPendingIntent = PendingIntent.getService(
            context,
            124,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_IMMUTABLE)
        val markAsReadAction = NotificationCompat.Action.Builder(
            R.drawable.ic_mobipay, "Mark as Read", markAsReadPendingIntent)
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
            .setShowsUserInterface(false)
            .build()
        return markAsReadAction
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun createMessagingStyle(
        context: Context, appConversation: MobiConversation): NotificationCompat.MessagingStyle {

        val appDeviceUser: MobiUser = MobiUser(1, "Kim", IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))

        val devicePerson = Person.Builder()

            .setName(appDeviceUser.name)

            .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))

            .setKey(appDeviceUser.id.toString())
            .build()

        val messagingStyle = NotificationCompat.MessagingStyle(devicePerson)

        messagingStyle.setConversationTitle(appConversation.title)

        messagingStyle.setGroupConversation(appConversation.recipients.size > 1)

        for (appMessage in appConversation.getUnreadMessages(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))) {

            val senderPerson = Person.Builder()
                .setName(appMessage.sender.name)
                .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))
                .setKey(appMessage.sender.id.toString())
                .build()

            messagingStyle.addMessage(
                appMessage.body, appMessage.timeReceived, senderPerson)
        }

        return messagingStyle
    }



    @RequiresApi(Build.VERSION_CODES.R)
    fun notify(context: Context, appConversation: MobiConversation) {

        val replyAction = createReplyAction(context, appConversation)
        val markAsReadAction = createMarkAsReadAction(context, appConversation)
        val messagingStyle = createMessagingStyle(context, appConversation)

        val notification = NotificationCompat.Builder(context, "payment_request")
            .setSmallIcon(R.drawable.ic_mobipay)
            .setCategory(CATEGORY_MESSAGE)
            .setLargeIcon(appConversation.icon)
            .setSilent(false)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_MAX)

            .addAction(replyAction)

            .build()


        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(appConversation.id, notification)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->

            Log.d(TAG, "Notification Title: ${notification.title}")
            Log.d(TAG, "Notification Body: ${notification.body}")

            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "페이로드: ${remoteMessage.data}")
            }

            confirmFCMReceived(remoteMessage.messageId ?: "No Message ID")

            val user2 = MobiUser(15, "MobiUserTMP", IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))
            val list2 = mutableListOf<MobiUser>()
            list2.add(user2)
            notify(applicationContext, MobiConversation(77, "Title1", list2, BitmapFactory.decodeResource(resources, R.drawable.ic_mobipay)))

            val intent2 = Intent("com.kimnlee.mobipay.SHOW_ALERT")
            intent2.putExtra("title", "모비페이 결제요청")
            intent2.putExtra("content", notification.body)
            sendBroadcast(intent2)
            Log.d(TAG, "onMessageReceived: sent Broadcast")
        }
    }

    /**
     * 서버에 FCM 정상 수신을 알리는 함수
     */
    private fun confirmFCMReceived(msgId: String) {
        val call = backendService.confirmFCMReceived(msgId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: FCM 수신 confirm 완료")
                } else {
                    Log.d(TAG, "onResponse: FCM 수신 confirm 실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "onFailure: FCM 수신 컨펌 서버 통신 오류 \n${Log.getStackTraceString(t)}")
            }
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: " + token)
        // 백엔드 서버에 FCM 토큰 전송
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val call = backendService.registerToken(token)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: FCM 토큰 정상 등록됨")
                } else {
                    Log.d(TAG, "onResponse: FCM 토큰 등록 실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "onFailure: FCM 토큰 서버 통신 오류 \n${Log.getStackTraceString(t)}")
            }
        })
    }
}
