package com.kimnlee.firebase

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.CarExtender
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.PaymentOperations
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.BackendService
import com.kimnlee.common.utils.AAFocusManager
import com.kimnlee.common.utils.MobiNotificationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import com.kimnlee.common.R
import com.kimnlee.common.utils.EXTRA_CONVERSATION_ID_KEY
import com.kimnlee.common.utils.MessagingService
import com.kimnlee.common.utils.MobiConversation
import com.kimnlee.common.utils.MobiUser
import com.kimnlee.common.utils.REMOTE_INPUT_RESULT_KEY
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.notification.data.Notification
import com.kimnlee.notification.data.NotificationDetails
import com.kimnlee.notification.data.NotificationType
import com.kimnlee.notification.data.NotificationRepository
import java.time.LocalDateTime
import kotlin.random.Random

private const val TAG = "FCMService"
private const val FCM_TYPE_MEMBER_INVITATION = "invitation"
private const val FCM_TYPE_PAYMENT_REQUEST = "transactionRequest"
private const val FCM_TYPE_PAYMENT_RESULT = "transactionResult"
private const val FCM_TYPE_PAYMENT_CANCEL = "transactionCancel"
private const val FCM_TYPE_AUTO_PAY_FAILURE = "autoPayFailed"

class FCMService : FirebaseMessagingService() {

    private var apiClient: ApiClient? = null
    private var authManager: AuthManager? = null
    private var paymentOperations: PaymentOperations? = null
    private var memberInvitationOperations: MemberInvitationOperations? = null
    private lateinit var mNotificationManager: NotificationManagerCompat

    private lateinit var notificationManager: MobiNotificationManager

    private lateinit var notificationRepository: NotificationRepository

    private val fcmApi: Retrofit? by lazy {
        apiClient?.fcmApi
    }
    private val backendService: BackendService? by lazy {
        fcmApi?.create(BackendService::class.java)
    }

    // Service는 인자로 전달하지 못한다고 해서 common 모듈에 FCMDependencyProvider 만들고
    // MobipayApplication에서 초기화된 apiClient, authManager, paymentRepository 인스턴스 가져오기
    private fun initializeDependencies() {
        val dependencyProvider = applicationContext as? FCMDependencyProvider
        if (dependencyProvider == null) {
            Log.e(TAG, "Application context does not implement FCMDependencyProvider")
            return
        }

        apiClient = dependencyProvider.apiClient
        authManager = dependencyProvider.authManager
        paymentOperations = dependencyProvider.paymentOperations
        memberInvitationOperations = dependencyProvider.memberInvitationOperations

        mNotificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager = MobiNotificationManager.getInstance(applicationContext)

    }

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

        initializeDependencies()
        notificationRepository = NotificationRepository(applicationContext)

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

        Log.d(TAG, "onCreate: BASE URL = ${fcmApi?.baseUrl()}")

    }

    fun processMessage(remoteMessage: RemoteMessage){
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "페이로드: ${remoteMessage.data}")

            Log.d(TAG, "processMessage: 안드로이드 오토 화면 켜져있는지 = ${AAFocusManager.isAppInFocus}")

            val responseJsonString = Gson().toJson(remoteMessage.data)

            val fcmData = Gson().fromJson(responseJsonString, FCMData::class.java)

            when (fcmData.type) {
                FCM_TYPE_PAYMENT_REQUEST -> {
                    val intent = Intent("com.kimnlee.mobipay.CLOSE_MENU")
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                    if (fcmData.lat != null && fcmData.lng != null) {

                        val notificationDetails = NotificationDetails(
                            merchantName = fcmData.merchantName,
                            paymentBalance = fcmData.paymentBalance,
                            info = fcmData.info,
                            body = null,
                            inviterName = null,
                            inviterPicture = null,
                            carNumber = null,
                            carModel = null
                        )
                        val notification = Notification(
                            details = notificationDetails,
                            timestamp = LocalDateTime.now(),
                            type = NotificationType.PAYMENT
                        )
                        notificationRepository.addPaymentRequestNotification(notification)

                        paymentOperations?.processFCM(fcmData)
                    }
                }
                FCM_TYPE_MEMBER_INVITATION -> {
                    val rjs = Gson().toJson(remoteMessage.data)
                    val fcmDataForInvitation = Gson().fromJson(rjs, FCMDataForInvitation::class.java)

                    val notificationDetails = NotificationDetails(
                        merchantName = null,
                        paymentBalance = null,
                        info = null,
                        body = fcmDataForInvitation.body,
                        inviterName = fcmDataForInvitation.inviterName,
                        inviterPicture = fcmDataForInvitation.inviterPicture,
                        carNumber = fcmDataForInvitation.carNumber,
                        carModel = fcmDataForInvitation.carModel
                    )
                    val notification = Notification(
                        details = notificationDetails,
                        timestamp = LocalDateTime.now(),
                        type = NotificationType.MEMBER
                    )
                    notificationRepository.addInvitationNotification(notification)

                    memberInvitationOperations?.processFCM(fcmDataForInvitation)
                }
                FCM_TYPE_PAYMENT_CANCEL -> {
                    // 결제 취소화면 표시
                    Log.d(TAG, "processMessage: 결제취소 요청 확인됨")

                    val fcmDataJson = Uri.encode(Gson().toJson(fcmData))
                    Log.d(TAG, "processMessage: $fcmDataJson")
                    val deepLinkUri = Uri.parse("mobipay://payment_cancelled?fcmData=$fcmDataJson")

                    val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    notificationManager.showNotification("모비페이 결제취소", "${fcmData.merchantName}\n${moneyFormat(fcmData.paymentBalance!!.toBigInteger())}", pendingIntent)
                    notificationManager.broadcastForPlainHUN("모비페이 결제취소", "${fcmData.merchantName}\n${moneyFormat(fcmData.paymentBalance!!.toBigInteger())}")
                }
                FCM_TYPE_AUTO_PAY_FAILURE -> {

                }
                FCM_TYPE_PAYMENT_RESULT -> {
                    val intent = Intent("com.kimnlee.mobipay.CLOSE_MENU")
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                    // 결제 결과화면 표시
                }

                else -> {
//                    Log.d(TAG, "processMessage: 이것은 ELSE에 속한다.")
//                    try{
//                        val rjs = Gson().toJson(remoteMessage.data)
//                        val fcmDataForInvitation = Gson().fromJson(rjs, FCMDataForInvitation::class.java)
//
//                        if (fcmDataForInvitation.title?.contains("초대") == true){
//                            memberInvitationOperations?.processFCM(fcmDataForInvitation)
//                        }
//
//                    }catch (e: Exception){
//                        e.printStackTrace()
//                    }
                }
            }

        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            processMessage(remoteMessage)
        }

        remoteMessage.data.let { data ->
            Log.d("FCM Serv", "Data payload: $data")

            if (data.containsKey("type") && data["type"] == "menuList") {
                val menusData = data["info"]

                val menuItems = menusData?.split("%") ?: listOf()

                val displayMenu = menuItems.joinToString(separator = "\n") { item ->
                    val details = item.split("#")
                    val name = details[0]  // 메뉴 이름
                    val price = details[1].toIntOrNull() ?: 0  // 가격 (문자열에서 Int로 변환)
                    "${name}: ${moneyFormat(price.toBigInteger())}"
                }

                val intent = Intent("com.kimnlee.testmsg.UPDATE_UI")
                intent.putExtra("menus", displayMenu)
                intent.putExtra("roomId", data["roomId"])

                val merchantName = data["merchantName"] ?: "모비페이 가맹점 메뉴 (음성주문 가능)"

                intent.putExtra("merchant_name", merchantName)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }

        remoteMessage.notification?.let { notification ->
            // Log notification details
            Log.d("FCM Serv", "Notification Title: ${notification.title}")
            Log.d("FCM Serv", "Notification Body: ${notification.body}")

            // Custom action based on notification
            // You can trigger an internal action or log this event
            confirmFCMReceived(remoteMessage.messageId ?: "No Message ID")

            val intent = Intent("com.kimnlee.testmsg.UPDATE_UI")
            intent.putExtra("new_text", notification.body)
//            sendNotification(1, notification.body ?: "SAMPLE", "TEST", System.currentTimeMillis())
//            sendCarNotification3("TEST", "안녕")
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: $token")
        // 백엔드 서버에 FCM 토큰 전송
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val call = backendService?.registerToken(token)
        call?.enqueue(object : Callback<Void> {
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

    private fun createReplyRemoteInput(context: Context): RemoteInput {
        // RemoteInput.Builder accepts a single parameter: the key to use to store
        // the response in.
        return RemoteInput.Builder(REMOTE_INPUT_RESULT_KEY).build()
        // Note that the RemoteInput has no knowledge of the conversation. This is
        // because the data for the RemoteInput is bound to the reply Intent using
        // static methods in the RemoteInput class.
    }

    private fun createReplyIntent(
        context: Context, appConversation: MobiConversation
    ): Intent {
        // Creates the intent backed by the MessagingService.
        val intent = Intent(context, MessagingService::class.java)

        // Lets the MessagingService know this is a reply request.
//        intent.action = ACTION_REPLY

        // Provides the ID of the conversation that the reply applies to.
        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)

        return intent
    }

    private fun createReplyAction(
        context: Context, appConversation: MobiConversation): NotificationCompat.Action {
        val replyIntent: Intent = createReplyIntent(context, appConversation)
        // ...
        // ...
        val replyPendingIntent = PendingIntent.getService(
            context,
//            createReplyId(appConversation), // Method explained later.
            155,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        // ...
        // ...
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_mobipay, "Reply", replyPendingIntent)
            // Provides context to what firing the Action does.
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)

            // The action doesn't show any UI, as required by Android Auto.
            .setShowsUserInterface(false)

            // Don't forget the reply RemoteInput. Android Auto will use this to
            // make a system call that will add the response string into
            // the reply intent so it can be extracted by the messaging app.
            .addRemoteInput(createReplyRemoteInput(context))
            .build()

        return replyAction
    }

    private fun createMarkAsReadIntent(
        context: Context, appConversation: MobiConversation): Intent {
        val intent = Intent(context, MessagingService::class.java)
//        intent.action = ACTION_MARK_AS_READ
        intent.putExtra(EXTRA_CONVERSATION_ID_KEY, appConversation.id)
        return intent
    }

    private fun createMarkAsReadAction(
        context: Context, appConversation: MobiConversation): NotificationCompat.Action {
        val markAsReadIntent = createMarkAsReadIntent(context, appConversation)
        val markAsReadPendingIntent = PendingIntent.getService(
            context,
//            createMarkAsReadId(appConversation), // Method explained below.
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
        // Method defined by the messaging app.
        val appDeviceUser: MobiUser = MobiUser(1, "Kim", IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))

        val devicePerson = Person.Builder()
            // The display name (also the name that's read aloud in Android auto).
            .setName(appDeviceUser.name)

            // The icon to show in the notification shade in the system UI (outside
            // of Android Auto).
            .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))

            // A unique key in case there are multiple people in this conversation with
            // the same name.
            .setKey(appDeviceUser.id.toString())
            .build()

        val messagingStyle = NotificationCompat.MessagingStyle(devicePerson)

        // Sets the conversation title. If the app's target version is lower
        // than P, this will automatically mark the conversation as a group (to
        // maintain backward compatibility). Use `setGroupConversation` after
        // setting the conversation title to explicitly override this behavior. See
        // the documentation for more information.
        messagingStyle.setConversationTitle(appConversation.title)

        // Group conversation means there is more than 1 recipient, so set it as such.
        messagingStyle.setGroupConversation(appConversation.recipients.size > 1)
        // ...
        for (appMessage in appConversation.getUnreadMessages(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))) {
            // The sender is also represented using a Person object.
            val senderPerson = Person.Builder()
                .setName(appMessage.sender.name)
                .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.ic_mobipay))
                .setKey(appMessage.sender.id.toString())
                .build()

            // Adds the message. More complex messages, like images,
            // can be created and added by instantiating the MessagingStyle.Message
            // class directly. See documentation for details.
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


//        val intent = Intent(context, HelloWorldScreen::class.java).apply {
//            // You can add extra data if needed
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        // Wrap the intent with a PendingIntent
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        // Creates the notification.
        val notification = NotificationCompat.Builder(context, "noti_a")
            // A required field for the Android UI.
            .setSmallIcon(R.drawable.ic_mobipay)
            .setCategory(CATEGORY_MESSAGE)
            // Shows in Android Auto as the conversation image.
            .setLargeIcon(appConversation.icon)
            .setSilent(false)
            .setVisibility(VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            // Adds MessagingStyle.
            .setStyle(messagingStyle)
//            .setPriority(PRIORITY_HIGH)
            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .extend(
//                CarExtender()
//                    .setColor(Color.YELLOW) // Set notification color
////                    .setContentTitle(title) // Customize title for car display
////                    .setContentText(content) // Customize content for car display
//                    .setLargeIcon(appConversation.icon)
//            )

            // Adds reply action.
            .addAction(replyAction)

//            .setContentIntent(pendingIntent) // Set the intent for notification click action
//            .setAutoCancel(true)
            // Makes the mark-as-read action invisible, so it doesn't appear
            // in the Android UI but the app satisfies Android Auto's
            // mark-as-read Action requirement. Both required actions can be made
            // visible or invisible; it is a stylistic choice.
            .addAction(markAsReadAction)

//            .extend(
//                CarAppExtender.Builder()
//                .addAction(R.drawable.ic_noti, "읽음으로 표시", pendingIntent)
//                .build())

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
            applicationContext.resources,
            R.drawable.ic_mobipay
        )

        // Add Android Auto car extensions
        val notification = builder
            .extend(
                CarExtender()
                    .setColor(Color.YELLOW) // Set notification color
//                    .setContentTitle(title) // Customize title for car display
//                    .setContentText(content) // Customize content for car display
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

    private fun confirmFCMReceived(msgId: String) {
        // Assuming you have a method in your BackendService to register the token
        val call = backendService?.confirmFCMReceived(msgId)
        call?.enqueue(object : Callback<Void> {
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
