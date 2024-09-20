package com.kimnlee.inferencetest

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var module: Module
    private lateinit var imageView: ImageView
    private lateinit var textViewResult: TextView
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        imageView = findViewById(R.id.image_view)
        textViewResult = findViewById(R.id.text_view_result)
        val buttonSelectImage: Button = findViewById(R.id.button_select_image)

        module = Module.load(assetFilePath("for_cpu.pt"))

        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            imageView.setImageBitmap(bitmap)
            imageView.visibility = ImageView.VISIBLE

            val result = performInference(bitmap)
            textViewResult.text = "Recognized License Plate: $result"
        }
    }

    private fun resizeAndPadBitmap(bitmap: Bitmap, desiredWidth: Int, desiredHeight: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val scale = Math.min(
            desiredWidth.toFloat() / originalWidth,
            desiredHeight.toFloat() / originalHeight
        )

        val scaledWidth = (scale * originalWidth).toInt()
        val scaledHeight = (scale * originalHeight).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        val paddedBitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedBitmap)
        canvas.drawColor(Color.BLACK)
        val left = (desiredWidth - scaledWidth) / 2.0f
        val top = (desiredHeight - scaledHeight) / 2.0f
        canvas.drawBitmap(scaledBitmap, left, top, null)

        return paddedBitmap
    }


    private fun bitmapToFloat32TensorGrayscale(bitmap: Bitmap): Tensor {
        val width = bitmap.width
        val height = bitmap.height
        val floatArray = FloatArray(1 * height * width)
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            
            val r = (pixel shr 16 and 0xff).toFloat()
            val g = (pixel shr 8 and 0xff).toFloat()
            val b = (pixel and 0xff).toFloat()
            
            val gray = 0.299f * r + 0.587f * g + 0.114f * b
            
            val normalized = (gray / 255.0f - 0.5f) / 0.5f
            floatArray[i] = normalized
        }
        val shape = longArrayOf(1, 1, height.toLong(), width.toLong())
        return Tensor.fromBlob(floatArray, shape)
    }

    private fun toGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayscaleBitmap
    }
    
    private fun performInference(bitmap: Bitmap): String {
        
        val processedBitmap = resizeAndPadBitmap(bitmap, desiredWidth = bitmap.width, desiredHeight = bitmap.height)
        
        val inputTensor = bitmapToFloat32TensorGrayscale(processedBitmap)

        val textArray = LongArray(1 * 10)
        val textShape = longArrayOf(1, 10)
        val textTensor = Tensor.fromBlob(textArray, textShape)

        val inputs = arrayOf(IValue.from(inputTensor), IValue.from(textTensor))
        val output = module.forward(*inputs)
        val outputTensor = output.toTensor()

        val scores = outputTensor.dataAsFloatArray

        val licensePlate = decodeLicensePlate(scores)

        return licensePlate
    }

    private fun decodeLicensePlate(scores: FloatArray): String {
        val characterSet = "0123456789가나다라마거너더러머어저고노도로모보오조소수구누두루무부우주바사아자하허호배육해공국합경전충남북기강원울제산인천광대전"
        val indices = scores.map { it.toInt() }
        val builder = StringBuilder()
        for (idx in indices) {
            if (idx >= 0 && idx < characterSet.length) {
                builder.append(characterSet[idx])
            }
        }
        return builder.toString()
    }



    private fun assetFilePath(assetName: String): String {
        val file = File(filesDir, assetName)
        if (!file.exists()) {
            assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
            }
        }
        return file.absolutePath
    }
}
