package com.kimnlee.common.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MoneyFormat : VisualTransformation {

    private val currencySymbol = "원"

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val digits = originalText.filter { it.isDigit() }
        val formattedDigits = formatWithCommas(digits)
        val formattedText = formattedDigits + currencySymbol

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = 0
                var digitsProcessed = 0

                while (digitsProcessed < offset && transformedOffset < formattedDigits.length) {
                    if (formattedDigits[transformedOffset] == ',') {
                        transformedOffset++
                    } else {
                        transformedOffset++
                        digitsProcessed++
                    }
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = 0
                var transformedOffset = 0

                while (transformedOffset < offset && transformedOffset < formattedDigits.length) {
                    if (formattedDigits[transformedOffset] == ',') {
                        transformedOffset++
                    } else {
                        transformedOffset++
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }

    private fun formatWithCommas(digits: String): String {
        if (digits.isEmpty()) return ""
        val sb = StringBuilder()
        var count = 0
        for (i in digits.length - 1 downTo 0) {
            sb.append(digits[i])
            count++
            if (count % 3 == 0 && i != 0) {
                sb.append(',')
            }
        }
        return sb.reverse().toString()
    }
}
