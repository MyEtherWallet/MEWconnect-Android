package com.myetherwallet.mewconnect.feature.scan.utils

import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode

/**
 * Created by BArtWell on 14.07.2018.
 */

class BarcodeTracker(private val listener: ((item: Barcode) -> Unit)) : Tracker<Barcode>() {

    override fun onNewItem(id: Int, item: Barcode) {
        listener.invoke(item)
    }
}
