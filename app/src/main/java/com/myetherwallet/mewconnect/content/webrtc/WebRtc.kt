package com.myetherwallet.mewconnect.content.webrtc

import android.content.Context
import com.myetherwallet.mewconnect.content.data.EncryptedMessage
import com.myetherwallet.mewconnect.content.data.Offer
import com.myetherwallet.mewconnect.content.data.TurnServer
import com.myetherwallet.mewconnect.core.utils.MewLog
import org.webrtc.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by BArtWell on 26.07.2018.
 */

private const val TAG = "WebRtc"
private const val ICE_SERVER_URL = "stun:global.stun.twilio.com:3478?transport=udp"
private const val DATA_CHANNEL_ID = "MEWRTCdC"
private val GATHERING_TIMEOUT = TimeUnit.SECONDS.toMillis(1)

class WebRtc {

    lateinit var connectSuccessListener: () -> Unit
    lateinit var connectErrorListener: () -> Unit
    lateinit var answerListener: (offer: Offer) -> Unit
    lateinit var dataListener: () -> Unit
    lateinit var messageListener: (data: String) -> Unit
    lateinit var disconnectListener: () -> Unit

    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private val mediaConstraints = MediaConstraints()
    private var isDataChannelOpened: Boolean = false
    private var gatheringTimer: Timer? = null

    fun connectWithOffer(context: Context, offer: Offer, turnServers: List<TurnServer>? = null) {
        val sessionDescription = offer.toSessionDescription()
        val iceServersList = mutableListOf<PeerConnection.IceServer>()
        if (turnServers == null) {
            MewLog.d(TAG, "Without turn servers")
            iceServersList.add(PeerConnection.IceServer
                    .builder(ICE_SERVER_URL)
                    .createIceServer())
        } else {
            MewLog.d(TAG, "With turn servers")
            iceServersList.add(PeerConnection.IceServer
                    .builder(turnServers[0].url)
                    .createIceServer())
            for (i in 1 until turnServers.size) {
                iceServersList.add(PeerConnection.IceServer
                        .builder(turnServers[i].url)
                        .setUsername(turnServers[i].username)
                        .setPassword(turnServers[i].credential)
                        .createIceServer())
            }
        }
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions())

        val peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(PeerConnectionFactory.Options())
                .createPeerConnectionFactory()
        val rtcConfig = PeerConnection.RTCConfiguration(iceServersList)

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, mediaConstraints, PeerConnectionObserver(::handleIceConnectionChange, ::handleIceGatheringChange))
        if (peerConnection == null) {
            connectErrorListener()
        } else {
            peerConnection?.setRemoteDescription(WebRtcSdpObserver("setRemoteDescription", ::createAnswer, {}), sessionDescription)
        }
    }

    private fun handleIceConnectionChange(connectionState: PeerConnection.IceConnectionState) {
        if (connectionState == PeerConnection.IceConnectionState.FAILED) {
            connectErrorListener()
        } else if (connectionState == PeerConnection.IceConnectionState.CONNECTED) {
            connectSuccessListener()
            val init = DataChannel.Init()
            init.id = 1
            dataChannel = peerConnection?.createDataChannel(DATA_CHANNEL_ID, init)
            dataChannel?.registerObserver(DataChannelObserver(::onDataChannelStateChange, ::onDataMessage))
            onDataChannelStateChange()
        } else if (connectionState == PeerConnection.IceConnectionState.CLOSED) {
            disconnect()
        } else if (connectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            disconnectListener()
        }
    }

    private fun handleIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {
        if (iceGatheringState == PeerConnection.IceGatheringState.GATHERING) {
            gatheringTimer = Timer()
            gatheringTimer?.schedule(object : TimerTask() {
                override fun run() {
                    generateAnswer(true)
                }
            }, GATHERING_TIMEOUT)
        } else if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
            gatheringTimer?.cancel()
            gatheringTimer = null
            generateAnswer(false)
        }
    }

    private fun generateAnswer(force: Boolean) {
        if (peerConnection != null &&
                peerConnection?.iceConnectionState() != PeerConnection.IceConnectionState.FAILED &&
                peerConnection?.iceConnectionState() != PeerConnection.IceConnectionState.CONNECTED &&
                (force || peerConnection?.iceGatheringState() == PeerConnection.IceGatheringState.COMPLETE)) {
            answerListener(Offer(peerConnection!!.localDescription))
        }
    }


    private fun onDataChannelStateChange() {
        if (dataChannel?.state() == DataChannel.State.OPEN) {
            isDataChannelOpened = true
            dataListener()
        } else if (dataChannel?.state() == DataChannel.State.CLOSED || dataChannel?.state() == DataChannel.State.CLOSING) {
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
        peerConnection?.createAnswer(WebRtcSdpObserver("createAnswer", ::setLocalDescription, connectErrorListener::invoke), mediaConstraints)
    }

    private fun setLocalDescription(sessionDescription: SessionDescription?) {
        if (sessionDescription == null) {
            connectErrorListener()
        } else {
            peerConnection?.setLocalDescription(WebRtcSdpObserver("setLocalDescription", { _ -> }, connectErrorListener::invoke), sessionDescription)
        }
    }

    fun send(data: EncryptedMessage) {
        if (dataChannel?.state() == DataChannel.State.OPEN) {
            dataChannel?.send(DataChannel.Buffer(ByteBuffer.wrap(data.toByteArray()), false))
        } else {
            MewLog.d(TAG, "Sent failed: data channel not opened")
        }
    }

    fun disconnect() {
        try {
            if (isDataChannelOpened) {
                isDataChannelOpened = false
                dataChannel?.close()
                dataChannel = null
            }
            peerConnection?.close()
            peerConnection = null
            gatheringTimer?.cancel()
            gatheringTimer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}