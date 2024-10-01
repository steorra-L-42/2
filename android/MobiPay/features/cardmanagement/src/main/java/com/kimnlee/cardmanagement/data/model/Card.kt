package com.kimnlee.cardmanagement.data.model

import java.util.Date

data class Card(
    val cardNo: String,
    val cvc: String,
    val withdrawalDate: String,
    val cardExpriyDate: String,
    val created : Date,
    val mobiUserId: Long,
    val accountId : Long,
    val cardUniqueNo: String
)
data class Photos(
    val albumId: Int,
    val id: Int,
    val title : String,
    val url : String,
    val thumbnailUrl : String
)
data class RegistrationCard(
    val mobiUserId: Long,
    val ownedCardId: Long,
    val oneDayLimit: Int,
    val oneTimeLimit: Int,
    val cardName: String,
    val autoPayStatus: Boolean,
)