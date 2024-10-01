package com.kimnlee.common.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.kimnlee.common.R

class MobiNotificationManager private constructor(private val applicationContext: Context) {

    companion object {
        @Volatile
        private var INSTANCE: MobiNotificationManager? = null

        fun getInstance(context: Context): MobiNotificationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MobiNotificationManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    fun createReplyRemoteInput(context: Context): RemoteInput {
        return RemoteInput.Builder(REMOTE_INPUT_RESULT_KEY).build()
    }

    fun createReplyIntent(
        context: Context, appConversation: MobiConversation
    ): Intent {

        val intent = Intent(context, MessagingService::class.java)

        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)

        return intent
    }


    fun createReplyAction(
        context: Context, appConversation: MobiConversation
    ): NotificationCompat.Action {
        val replyIntent: Intent = createReplyIntent(context, appConversation)

        val replyPendingIntent = PendingIntent.getService(
            context,
            155,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_mobipay, "답장", replyPendingIntent)
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
            .setShowsUserInterface(false)
            .addRemoteInput(createReplyRemoteInput(context))
            .build()

        return replyAction
    }

    fun createMarkAsReadIntent(
        context: Context, appConversation: MobiConversation
    ): Intent {
        val intent = Intent(context, MessagingService::class.java)
        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)
        return intent
    }

    fun createMarkAsReadAction(
        context: Context, appConversation: MobiConversation
    ): NotificationCompat.Action {
        val markAsReadIntent = createMarkAsReadIntent(context, appConversation)
        val markAsReadPendingIntent = PendingIntent.getService(
            context,
            124,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_IMMUTABLE)
        val markAsReadAction = NotificationCompat.Action.Builder(
            R.drawable.ic_mobipay, "읽음으로 표시", markAsReadPendingIntent)
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
            .setShowsUserInterface(false)
            .build()
        return markAsReadAction
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun createMessagingStyle(
        context: Context, appConversation: MobiConversation
    ): NotificationCompat.MessagingStyle {

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


//    fun notify(context: Context, appConversation: MobiConversation) {
    fun notify(appConversation: MobiConversation) {

        val replyAction = createReplyAction(applicationContext, appConversation)
        val markAsReadAction = createMarkAsReadAction(applicationContext, appConversation)

        var messagingStyle: NotificationCompat.MessagingStyle? = null

        if (Build.VERSION.SDK_INT > 30) {
            messagingStyle = createMessagingStyle(applicationContext, appConversation)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "payment_request")
            .setSmallIcon(R.drawable.ic_mobipay)
            .setCategory(CATEGORY_MESSAGE)
            .setLargeIcon(appConversation.icon)
            .setSilent(false)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_MAX)

            .addAction(replyAction)
            .addAction(markAsReadAction)

        messagingStyle?.let {
            notificationBuilder.setStyle(it)
        }

        val notification = notificationBuilder.build()

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(appConversation.id, notification)
        }
    }

    fun showNotification(title: String, message: String, pendingIntent: PendingIntent) {
        val channelId = "payment_request"
//        val intent = Intent(context, YourTargetActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_mobipay)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(123, notification) // Unique ID for the notification
    }
}