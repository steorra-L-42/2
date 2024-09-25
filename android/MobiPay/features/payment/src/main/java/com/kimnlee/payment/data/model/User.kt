package com.kimnlee.payment.data.model

// merchant 가맹점
/*
    merchant_id Long 가맹점 PK
    category_id Varchar(20) 카테고리 ID
    merchant_name Varchar(100) 가맹점명
    lat Double 위도
    lng Double 경도
    mobi_api_key Varchar(20) api key
*/

data class Merchant(
    val merchant_id: Long,
    val category_id: String,
    val merchant_name: String,
    val lat: Double,
    val lng: Double,
    val mobi_api_key: String
)

// merchant_transaction 가맹점 결제 내역
/*
    transaction_unique_no 고유 거래번호 Long
    transaction_date 거래일자 Varchar 8
    transaction_time 거래시각 Varchar 6
    payment_balance 거래금액 Long
    info 거래 항목 Text
    cancelled 취소 Boolean
    merchant_id : 가맹점 FK Long
    mobi_user_id : 사용자 FK Long
    owned_card_id : 소유한 카드 PK Long
*/

data class MerchantTransaction(
    val transaction_unique_no: Long,
    val transaction_date: String, // Format: YYYYMMDD
    val transaction_time: String, // Format: HHMMSS
    val payment_balance: Long,
    val info: String,
    val cancelled: Boolean = false,
    val merchant_id: Long, // Foreign key
    val mobi_user_id: Long, // Foreign key
    val owned_card_id: Long // Primary key
)

// dummy api
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Address,
    val phone: String,
    val website: String,
    val company: Company
)
data class Photos(
    val albumId: Int,
    val id: Int,
    val title : String,
    val url : String,
    val thumbnailUrl : String
)
data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo
)

data class Geo(
    val lat: String,
    val lng: String
)

data class Company(
    val name: String,
    val catchPhrase: String,
    val bs: String
)