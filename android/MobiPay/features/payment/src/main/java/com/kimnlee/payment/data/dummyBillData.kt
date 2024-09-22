package com.kimnlee.payment.data

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

val dummyMerchants = listOf(
        Merchant(1L,"CAT001", "Starbucks", 37.5665, 126.9780, "APIKEY001"),
        Merchant(2L,"CAT002", "McDonald's", 37.5700, 126.9768, "APIKEY002"),
        Merchant(3L,"CAT003", "Burger King", 37.5650, 126.9820, "APIKEY003"),
        Merchant(4L,"CAT004", "KFC", 37.5711, 126.9774, "APIKEY004"),
        Merchant(5L,"CAT005", "Subway", 37.5688, 126.9815, "APIKEY005"),
        Merchant(6L,"CAT006", "Domino's Pizza", 37.5628, 126.9762, "APIKEY006"),
        Merchant(7L,"CAT007", "Dunkin' Donuts", 37.5647, 126.9786, "APIKEY007"),
        Merchant(8L,"CAT008", "Baskin Robbins", 37.5671, 126.9795, "APIKEY008"),
        Merchant(9L,"CAT009", "7-Eleven", 37.5638, 126.9831, "APIKEY009"),
        Merchant(10L,"CAT010", "Pizza Hut", 37.5695, 126.9779, "APIKEY010")
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

val dummyTransactions = listOf(
    MerchantTransaction(
        transaction_unique_no = 1000001L,
        transaction_date = "20240901",
        transaction_time = "103015",
        payment_balance = 5500L,
        info = "일시불",
        cancelled = false,
        merchant_id = 1L,
        mobi_user_id = 101L,
        owned_card_id = 1001L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000002L,
        transaction_date = "20240902",
        transaction_time = "121530",
        payment_balance = 8200L,
        info = "할부",
        cancelled = false,
        merchant_id = 2L,
        mobi_user_id = 102L,
        owned_card_id = 1002L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000003L,
        transaction_date = "20240903",
        transaction_time = "145000",
        payment_balance = 12000L,
        info = "할부",
        cancelled = true,
        merchant_id = 3L,
        mobi_user_id = 103L,
        owned_card_id = 1003L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000004L,
        transaction_date = "20240904",
        transaction_time = "093000",
        payment_balance = 3000L,
        info = "일시불",
        cancelled = false,
        merchant_id = 4L,
        mobi_user_id = 104L,
        owned_card_id = 1004L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000005L,
        transaction_date = "20240905",
        transaction_time = "170000",
        payment_balance = 20000L,
        info = "일시불",
        cancelled = false,
        merchant_id = 5L,
        mobi_user_id = 105L,
        owned_card_id = 1005L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000006L,
        transaction_date = "20240906",
        transaction_time = "180500",
        payment_balance = 15000L,
        info = "할부",
        cancelled = true,
        merchant_id = 6L,
        mobi_user_id = 106L,
        owned_card_id = 1006L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000007L,
        transaction_date = "20240907",
        transaction_time = "112000",
        payment_balance = 6500L,
        info = "일시불",
        cancelled = false,
        merchant_id = 7L,
        mobi_user_id = 107L,
        owned_card_id = 1007L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000008L,
        transaction_date = "20240908",
        transaction_time = "130000",
        payment_balance = 9000L,
        info = "할부",
        cancelled = false,
        merchant_id = 8L,
        mobi_user_id = 108L,
        owned_card_id = 1008L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000009L,
        transaction_date = "20240909",
        transaction_time = "140000",
        payment_balance = 30000L,
        info = "일시불",
        cancelled = true,
        merchant_id = 9L,
        mobi_user_id = 109L,
        owned_card_id = 1009L
    ),
    MerchantTransaction(
        transaction_unique_no = 1000010L,
        transaction_date = "20240910",
        transaction_time = "153000",
        payment_balance = 5000L,
        info = "일시불",
        cancelled = false,
        merchant_id = 10L,
        mobi_user_id = 110L,
        owned_card_id = 1010L
    )
)
