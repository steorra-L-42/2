package com.kimnlee.data.model

import com.google.gson.annotations.SerializedName

data class OCRResponse(
    @SerializedName("predicted_text") val predictedText: String?,
    @SerializedName("confidence") val confidence: Double?
)
