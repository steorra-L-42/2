package com.kimnlee.firebase

import android.app.IntentService
import android.content.Intent

const val EXTRA_CONVERSATION_ID_KEY = "conversation_id"
const val REMOTE_INPUT_RESULT_KEY = "reply_input"

class MessagingService : IntentService("MessagingService") {
    override fun onHandleIntent(intent: Intent?) {
        //TODO
    }
}