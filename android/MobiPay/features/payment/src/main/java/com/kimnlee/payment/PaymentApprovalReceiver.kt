package com.kimnlee.payment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDependencyProvider

private const val TAG = "PaymentApprovalReceiver"
class PaymentApprovalReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: Broadcast Receive 했다.")
        if (intent?.action == "com.kimnlee.mobipay.PAYMENT_APPROVAL" && context != null) {
            Log.d("PaymentApprovalReceiver", "사용자가 결제 승인함")

            val paymentOperations = (context.applicationContext as? FCMDependencyProvider)?.paymentOperations

            if (paymentOperations != null) {
                val fcmData = intent.getSerializableExtra("fcmData") as? FCMData

                if (fcmData != null) {
                    paymentOperations.processPay(fcmData, false)
                }
            } else {
                Log.e(TAG, "PaymentOperations 인스턴스 안 넘어옴")
            }
        }
    }
}
