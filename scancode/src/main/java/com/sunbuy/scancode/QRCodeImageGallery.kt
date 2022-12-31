package com.sunbuy.scancode

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QRCodeImageGallery(
    private val barcodeFormats: IntArray,
    private val onSuccess: ((Barcode) -> Unit),
    private val notFound: (() -> Unit),
    private val onFailure: ((Exception) -> Unit),
) {

    private val barcodeScanner by lazy {
        val optionsBuilder = if (barcodeFormats.size > 1) {
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(barcodeFormats.first(), *barcodeFormats.drop(1).toIntArray())
        } else {
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(barcodeFormats.firstOrNull() ?: Barcode.FORMAT_UNKNOWN)
        }
        try {
            BarcodeScanning.getClient(optionsBuilder.build())
        } catch (e: Exception) { // catch if for some reason MlKitContext has not been initialized
            onFailure(e)
            null
        }
    }

    fun onProcess(inputImage: InputImage) {
        barcodeScanner?.let { scanner ->
            scanner.process(inputImage)
                .addOnSuccessListener { codes ->
                    if (!codes.isNullOrEmpty()) {
                        codes.firstNotNullOfOrNull { it }?.let { onSuccess(it) }
                    } else {
                        notFound.invoke()
                    }
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        }
    }
}