package com.myetherwallet.mewconnect.content.webrtc

import com.myetherwallet.mewconnect.core.utils.MewLog
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * Created by BArtWell on 27.07.2018.
 */
class WebRtcSdpObserver(private val tag: String) : SdpObserver {

    private var onSetSuccessListener: (() -> Unit)? = null
    private var onSetErrorListener: (() -> Unit)? = null
    private var onCreateSuccessListener: ((sessionDescription: SessionDescription?) -> Unit)? = null
    private var onCreateErrorListener: (() -> Unit)? = null

    constructor(tag: String, successListener: () -> Unit, errorListener: () -> Unit) : this(tag) {
        onSetSuccessListener = successListener
        onSetErrorListener = errorListener
    }

    constructor(tag: String, successListener: (sessionDescription: SessionDescription?) -> Unit, errorListener: () -> Unit) : this(tag) {
        onCreateSuccessListener = successListener
        onCreateErrorListener = errorListener
    }

    override fun onSetSuccess() {
        MewLog.d(tag, "SdpObserver.onSetSuccess")
        onSetSuccessListener?.invoke()
    }

    override fun onSetFailure(reason: String) {
        MewLog.d(tag, "SdpObserver.onSetFailure")
        onSetErrorListener?.invoke()
    }

    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
        MewLog.d(tag, "SdpObserver.onCreateSuccess")
        onCreateSuccessListener?.invoke(sessionDescription)
    }

    override fun onCreateFailure(reason: String) {
        MewLog.d(tag, "SdpObserver.onCreateFailure")
    }
}