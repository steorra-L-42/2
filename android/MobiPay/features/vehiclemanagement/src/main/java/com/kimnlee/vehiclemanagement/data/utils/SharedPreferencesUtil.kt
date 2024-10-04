package com.kimnlee.vehiclemanagement.data.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {
    private const val PREF_NAME = "VehicleManagementPrefs"
    private const val KEY_AUTO_PAYMENT = "auto_payment_status"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getAutoPaymentStatus(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_AUTO_PAYMENT, false)
    }

    fun setAutoPaymentStatus(context: Context, status: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_AUTO_PAYMENT, status).apply()
    }
}