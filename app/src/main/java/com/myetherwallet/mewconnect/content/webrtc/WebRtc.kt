package com.myetherwallet.mewconnect.content.webrtc

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.myetherwallet.mewconnect.content.data.EncryptedMessage
import com.myetherwallet.mewconnect.content.data.Offer
import com.myetherwallet.mewconnect.core.utils.MewLog
import org.webrtc.*
import java.nio.ByteBuffer

/**
 * Created by BArtWell on 26.07.2018.
 */

private const val TAG = "WebRtc"
private const val ICE_SERVER_URL = "stun:global.stun.twilio.com:3478?transport=udp"
private const val ON_ICE_CANDIDATE_DELAY = 200L
private const val DATA_CHANNEL_ID = "MEWRTCdC"

class WebRtc {

    private lateinit var peerConnection: PeerConnection
    private lateinit var dataChannel: DataChannel
    private val mediaConstraints = MediaConstraints()
    private val handler = Handler(Looper.getMainLooper())

    lateinit var connectSuccessListener: (offer: Offer) -> Unit
    lateinit var connectErrorListener: () -> Unit
    lateinit var dataListener: () -> Unit
    lateinit var messageListener: (data: String) -> Unit
    lateinit var disconnectListener: () -> Unit

    fun connectWithOffer(context: Context, offer: Offer) {
        val sessionDescription = offer.toSessionDescription()
        val iceServer = PeerConnection.IceServer
                .builder(ICE_SERVER_URL)
                .createIceServer()
        val iceServersList = listOf<PeerConnection.IceServer>(iceServer)
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions())
        val peerConnectionFactory = PeerConnectionFactory(PeerConnectionFactory.Options())
        val rtcConfig = PeerConnection.RTCConfiguration(iceServersList)

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, mediaConstraints, PeerConnectionObserver(::waitForAllIceCandidates, ::handleIceConnectionChange))

        peerConnection.setRemoteDescription(WebRtcSdpObserver("setRemoteDescription", ::createAnswer), sessionDescription)
    }

    private fun waitForAllIceCandidates() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            MewLog.d(TAG, "onIceCandidate")
            connectSuccessListener(Offer(peerConnection.localDescription))
        }, ON_ICE_CANDIDATE_DELAY)
    }

    private fun handleIceConnectionChange(connectionState: PeerConnection.IceConnectionState) {
        if (connectionState == PeerConnection.IceConnectionState.FAILED) {
            connectErrorListener()
        } else if (connectionState == PeerConnection.IceConnectionState.CONNECTED) {
            val init = DataChannel.Init()
            init.id = 1
            dataChannel = peerConnection.createDataChannel(DATA_CHANNEL_ID, init)
            dataChannel.registerObserver(DataChannelObserver(::onDataChannelStateChange, ::onDataMessage))
        } else if (connectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            disconnectListener()
        }
    }

    private var isDataChannelOpened: Boolean = false

    private fun onDataChannelStateChange() {
        if (dataChannel.state() == DataChannel.State.OPEN) {
            isDataChannelOpened = true
            dataListener()
        } else if (dataChannel.state() == DataChannel.State.CLOSED || dataChannel.state() == DataChannel.State.CLOSING) {
            isDataChannelOpened = false
        }
    }

    private fun onDataMessage(buffer: DataChannel.Buffer) {
        val data = buffer.data
        val bytes = ByteArray(data.capacity())
        data.get(bytes)
        if (buffer.binary) {
            MewLog.e(TAG, "Message is binary")
        } else {
            val message = String(bytes)
            MewLog.d(TAG, "Message text: $message")
            messageListener(String(bytes))
        }
    }

    private fun createAnswer() {
        peerConnection.createAnswer(WebRtcSdpObserver("createAnswer", ::setLocalDescription), mediaConstraints)
    }

    private fun setLocalDescription(sessionDescription: SessionDescription) {
        peerConnection.setLocalDescription(WebRtcSdpObserver("setLocalDescription"), sessionDescription)
    }

    fun send(data: EncryptedMessage) {
        dataChannel.send(DataChannel.Buffer(ByteBuffer.wrap(data.toByteArray()), false))
    }

    fun disconnect() {
        if (isDataChannelOpened) {
            isDataChannelOpened = false
            dataChannel.close()
            dataChannel.dispose()
        }
    }
}