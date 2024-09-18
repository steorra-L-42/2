package com.kimnlee.firebase

import android.graphics.Bitmap
import androidx.core.graphics.drawable.IconCompat

data class MobiConversation(
    val id: Int,
    val title: String,
    val recipients: MutableList<MobiUser>,
    val icon: Bitmap
) {
    
    fun getUnreadMessages(icon: IconCompat): List<MobiMessage> {
        val tmp = MobiMessage(1, MobiUser(55, "모비페이",  icon), "메세지 내용", System.currentTimeMillis())
        val list1 = mutableListOf<MobiMessage>()
        list1.add(tmp)
        return list1
    }
}
data class MobiUser(val id: Int, val name: String, val icon: IconCompat)
data class MobiMessage(
    val id: Int,
    val sender: MobiUser,
    val body: String,
    val timeReceived: Long)