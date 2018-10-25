package com.myetherwallet.mewconnect.content.webrtc

import com.myetherwallet.mewconnect.core.utils.MewLog
import org.webrtc.DataChannel

/**
 * Created by BArtWell on 27.07.2018.
 */

private const val TAG = "DataChannelObserver"

open class DataChannelObserver(
        private val onStateChangeListener: () -> Unit,
        private val onMessageListener: (buffer: DataChannel.Buffer) -> Unit
) : DataChannel.Observer {

    override fun onMessage(buffer: DataChannel.Buffer) {
        MewLog.d(TAG, "onMessage")
        onMessageListener.invoke(buffer)
    }

    override fun onBufferedAmountChange(amout: Long) {
        MewLog.d(TAG, "onBufferedAmountChange")
    }

    override fun onStateChange() {
        MewLog.d(TAG, "onStateChange")
        onStateChangeListener.invoke()
    }
}