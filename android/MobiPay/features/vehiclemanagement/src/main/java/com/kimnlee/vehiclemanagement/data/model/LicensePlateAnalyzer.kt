package com.kimnlee.vehiclemanagement.data.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.auth.model.OCRResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private const val TAG = "LicensePlateAnalyzer"
private const val INFERENCE_INTERVAL = 1000

class LicensePlateAnalyzer(
    private val context: Context,
    private val isAnalyzing: () -> Boolean,
    private val onLicensePlateRecognized: (String) -> Unit,
    private val apiClient: ApiClient
) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTime = 0L

    override fun analyze(imageProxy: ImageProxy) {
        if (!isAnalyzing()) {
            imageProxy.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAnalyzedTime >= INFERENCE_INTERVAL) {
            lastAnalyzedTime = currentTime

            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap != null) {
                // 이미지를 파일로 저장
                val file = File(context.cacheDir, "image.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()

                // 서버에 추론 요청
                sendImageToBackend(file) { recognizedText, confidence ->
                    if (confidence > 0.85) {
                        onLicensePlateRecognized(recognizedText)
                    }
                }
            }

            imageProxy.close()
        } else {
            imageProxy.close()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmap = imageProxyToBitmapInternal(imageProxy)

        return if (bitmap != null) {
            rotateBitmap(bitmap, rotationDegrees.toFloat())
        } else {
            null
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun imageProxyToBitmapInternal(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null

        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(
            nv21,
            ImageFormat.NV21,
            image.width,
            image.height,
            null
        )

        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, yuvImage.width, yuvImage.height),
            100,
            out
        )
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
    }

    private fun sendImageToBackend(file: File, callback: (String, Double) -> Unit) {

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val call = apiClient.ocrService.uploadImage(body)
        call.enqueue(object : Callback<OCRResponse> {
            override fun onResponse(call: Call<OCRResponse>, response: Response<OCRResponse>) {
                if (response.isSuccessful) {
                    val ocrResult = response.body()
                    Log.d(TAG, "onResponse: ${ocrResult.toString()}")
                    if (ocrResult != null && ocrResult.predictedText != "None" && (ocrResult.confidence
                            ?: 0.0) > 0.85
                    ) {
                        callback(ocrResult.predictedText ?: "", ocrResult.confidence ?: 0.0)
                    } else {
                        Log.e("onResponse", "에러 - 정확도가 낮거나 번호판 감지 안 됨")
                    }
                } else {
                    Log.e("onResponse", "에러 - ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OCRResponse>, t: Throwable) {
                Log.e("onFailure", "서버에 요청 자체가 실패 - ${t.message}")
            }
        })
    }
}