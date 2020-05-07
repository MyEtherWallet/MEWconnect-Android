package com.myetherwallet.mewconnect.feature.scan.view

import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.myetherwallet.mewconnect.core.utils.MewLog
import java.io.IOException

/**
 * Created by BArtWell on 06.05.2020.
 */

private const val TAG = "BarcodeScanningProcessor"

class BarcodeScanningProcessor(private val onBarCodeDetected: (FirebaseVisionBarcode) -> Unit) : VisionProcessorBase<List<FirebaseVisionBarcode>>() {

    private val detector: FirebaseVisionBarcodeDetector by lazy { FirebaseVision.getInstance().visionBarcodeDetector }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            MewLog.e(TAG, "Can't stop Detector", e)
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(results: List<FirebaseVisionBarcode>) {
        if (results.isNotEmpty()) {
            onBarCodeDetected(results[0])
        }
    }

    override fun onFailure(e: Exception) {
        MewLog.e(TAG, "Barcode detection failed", e)
    }
}
