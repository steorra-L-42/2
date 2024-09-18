package com.kimnlee.firebase

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.Person
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.CarExtender
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kimnlee.api.network.ApiClient
import com.kimnlee.api.network.BackendService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kimnlee.mobipay.R


private const val TAG = "MyFirebaseMessagingServ"

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var mNotificationManager: NotificationManagerCompat

    override fun onCreate() {
        Log.d(TAG, "onCreate: FCM onCreate")
        mNotificationManager = NotificationManagerCompat.from(applicationContext)
    }

    private val retrofit = ApiClient.retrofit

    private val backendService = retrofit.create(BackendService::class.java)

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
        // Creates the actions and MessagingStyle.
        val replyAction = createReplyAction(context, appConversation)
        val markAsReadAction = createMarkAsReadAction(context, appConversation)
        val messagingStyle = createMessagingStyle(context, appConversation)

        // Creates the notification.
        val notification = NotificationCompat.Builder(context, "noti_a")
            .setSmallIcon(R.drawable.ic_mobipay)
            .setCategory(CATEGORY_MESSAGE)
            .setLargeIcon(appConversation.icon)
            .setSilent(false)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_MAX)

            // Adds reply action.
            .addAction(replyAction)

            .build()

        // Posts the notification for the user to see.
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(appConversation.id, notification)

    }


    fun sendCarNotification(title: String, content: String) {
        // Create a builder for the notification
        val builder = NotificationCompat.Builder(applicationContext, "noti_a")
            .setSmallIcon(R.drawable.ic_mobipay) // Notification icon
            .setContentTitle(title) // Notification title
            .setContentText(content) // Notification content
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for visibility on Auto

        val icon = BitmapFactory.decodeResource(
            applicationContext.getResources(),
            R.drawable.ic_mobipay
        )

        // Add Android Auto car extensions
        val notification = builder
            .extend(
                CarExtender()
                    .setColor(Color.YELLOW) // Set notification color
                    .setLargeIcon(icon)
            )
            .build()

        // Notify using NotificationManagerCompat for Android Auto compatibility
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(1, notification) // Post the notification
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Serv", "From: ${remoteMessage.from}")
        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            // Log notification details
            Log.d("FCM Serv", "Notification Title: ${notification.title}")
            Log.d("FCM Serv", "Notification Body: ${notification.body}")

            // Custom action based on notification
            // You can trigger an internal action or log this event
            confirmFCMReceived(remoteMessage.messageId ?: "No Message ID")

            val user2 = MobiUser(15, "MobiUserTMP", IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))
            val list2 = mutableListOf<MobiUser>()
            list2.add(user2)
            notify(applicationContext, MobiConversation(77, "Title1", list2, BitmapFactory.decodeResource(resources, R.drawable.ic_mobipay)))

            val intent2 = Intent("com.mobi.testnavi.SHOW_ALERT")
            intent2.putExtra("title", "모비페이 결제요청")
            intent2.putExtra("content", notification.body)
            sendBroadcast(intent2)
            Log.d(TAG, "onMessageReceived: sent Broadcast")
        }
    }

    private fun confirmFCMReceived(msgId: String) {
        // Assuming you have a method in your BackendService to register the token
        val call = backendService.confirmFCMReceived(msgId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle successful registration (e.g., log it or notify the user)
                } else {
                    // Handle unsuccessful registration (e.g., log the error)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure (e.g., log the error or retry)
            }
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: " + token)
        // Send the new token to your backend server
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Assuming you have a method in your BackendService to register the token
        val call = backendService.registerToken(token)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle successful registration (e.g., log it or notify the user)
                } else {
                    // Handle unsuccessful registration (e.g., log the error)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure (e.g., log the error or retry)
            }
        })
    }
}
