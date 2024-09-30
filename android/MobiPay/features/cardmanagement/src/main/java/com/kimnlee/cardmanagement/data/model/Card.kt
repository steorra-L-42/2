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