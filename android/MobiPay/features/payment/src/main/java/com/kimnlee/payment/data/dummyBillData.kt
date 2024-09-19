package com.kimnlee.payment.data

// merchant_transaction
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

val dummyBillData = listOf(
    mapOf(
        "transaction_unique_no" to 1,
        "store_name" to "스타벅스",
        "transaction_date" to "2024-09-01",
        "transaction_time" to "10:30 AM",
        "info" to "일시불",
        "payment_balance" to 5500
    ),
    mapOf(
        "transaction_unique_no" to 2,
        "store_name" to "이마트",
        "transaction_date" to "2024-09-02",
        "transaction_time" to "02:45 PM",
        "info" to "일시불",
        "payment_balance" to 25000
    ),
    mapOf(
        "transaction_unique_no" to 3,
        "store_name" to "GS25",
        "transaction_date" to "2024-09-03",
        "transaction_time" to "09:15 PM",
        "info" to "일시불",
        "payment_balance" to 3200
    ),
    mapOf(
        "transaction_unique_no" to 4,
        "store_name" to "맥도날드",
        "transaction_date" to "2024-09-04",
        "transaction_time" to "07:00 PM",
        "info" to "일시불",
        "payment_balance" to 7800
    ),
    mapOf(
        "transaction_unique_no" to 5,
        "store_name" to "CU 편의점",
        "transaction_date" to "2024-09-05",
        "transaction_time" to "11:20 AM",
        "info" to "일시불",
        "payment_balance" to 4300
    ),
    mapOf(
        "transaction_unique_no" to 6,
        "store_name" to "스타벅스",
        "transaction_date" to "2024-09-01",
        "transaction_time" to "10:30 AM",
        "info" to "일시불",
        "payment_balance" to 5500
    ),
    mapOf(
        "transaction_unique_no" to 7,
        "store_name" to "이마트",
        "transaction_date" to "2024-09-02",
        "transaction_time" to "02:45 PM",
        "info" to "일시불",
        "payment_balance" to 25000
    ),
    mapOf(
        "transaction_unique_no" to 8,
        "store_name" to "GS25",
        "transaction_date" to "2024-09-03",
        "transaction_time" to "09:15 PM",
        "info" to "일시불",
        "payment_balance" to 3200
    ),
    mapOf(
        "transaction_unique_no" to 9,
        "store_name" to "맥도날드",
        "transaction_date" to "2024-09-04",
        "transaction_time" to "07:00 PM",
        "info" to "일시불",
        "payment_balance" to 7800
    ),
    mapOf(
        "transaction_unique_no" to 10,
        "store_name" to "CU 편의점",
        "transaction_date" to "2024-09-05",
        "transaction_time" to "11:20 AM",
        "info" to "일시불",
        "payment_balance" to 4300
    )
)
