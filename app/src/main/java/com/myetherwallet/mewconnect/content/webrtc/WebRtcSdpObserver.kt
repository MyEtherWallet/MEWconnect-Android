package com.myetherwallet.mewconnect.content.webrtc

import com.myetherwallet.mewconnect.core.utils.MewLog
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * Created by BArtWell on 27.07.2018.
 */
class WebRtcSdpObserver(private val tag: String) : SdpObserver {

    private var onSetSuccessListener: (() -> Unit)? = null
    private var onCreateSuccessListener: ((sessionDescription: SessionDescription) -> Unit)? = null

    constructor(tag: String, listener: () -> Unit) : this(tag) {
        onSetSuccessListener = listener
    }

    constructor(tag: String, listener: (sessionDescription: SessionDescription) -> Unit) : this(tag) {
        onCreateSuccessListener = listener
    }

    override fun onSetSuccess() {
        MewLog.d(tag, "SdpObserver.onSetSuccess")
        onSetSuccessListener?.invoke()
    }

    override fun onSetFailure(reason: String) {
        MewLog.d(tag, "SdpObserver.onSetFailure")
    }

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        MewLog.d(tag, "SdpObserver.onCreateSuccess")
        onCreateSuccessListener?.invoke(sessionDescription)
    }

    override fun onCreateFailure(reason: String) {
        MewLog.d(tag, "SdpObserver.onCreateFailure")
    }
}