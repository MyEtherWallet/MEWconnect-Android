package com.myetherwallet.mewconnect.feature.scan.utils

import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode

/**
 * Created by BArtWell on 14.07.2018.
 */

class BarcodeTrackerFactory(private val listener: ((item: Barcode) -> Unit)) : MultiProcessor.Factory<Barcode> {

    override fun create(barcode: Barcode): Tracker<Barcode> {
        return BarcodeTracker(listener)
    }
}
