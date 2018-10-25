package com.myetherwallet.mewconnect.feature.scan.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.content.data.*
import com.myetherwallet.mewconnect.content.gson.JsonParser
import com.myetherwallet.mewconnect.content.webrtc.WebRtc
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.MessageCrypt
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by BArtWell on 17.07.2018.
 */

private const val TAG = "SocketService"

private const val VERSION = "0.0.1"

private const val EXTRA_START = "start"
private const val EXTRA_SHUTDOWN_DELAYED = "shutdown_delayed"

private const val EVENT_HANDSHAKE = "handshake"
private const val EVENT_OFFER = "offer"
private const val EVENT_ANSWER = "answer"
private const val EVENT_ERROR = "error"
private const val EVENT_INVALID_CONNECTION = "InvalidConnection"
private const val EVENT_CONFIRMATION_FAILED = "confirmationFailed"

private const val EMIT_SIGNATURE = "signature"
private const val EMIT_ANSWER_SIGNAL = "answerSignal"
private const val EMIT_TRY_TURN = "tryTurn"
private const val EMIT_RTC_CONNECTED = "rtcConnected"

class SocketService : Service() {

    companion object {

        private val CONNECT_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
        private val SHUTDOWN_DELAY = TimeUnit.MINUTES.toMillis(5)

        init {
            Security.addProvider(BouncyCastleProvider())
        }

        fun getIntent(context: Context) = Intent(context, SocketService::class.java)

        fun start(context: Context) {
            val intent = getIntent(context)
            intent.putExtra(EXTRA_START, true)
            context.startService(intent)
        }

        fun shutdownDelayed(context: Context) {
            val intent = getIntent(context)
            intent.putExtra(EXTRA_SHUTDOWN_DELAYED, true)
            context.startService(intent)
        }
    }

    @Inject
    lateinit var preferences: PreferencesManager
    var isConnected = false
    private val binder = ServiceBinder(this)
    private val handler = Handler()
    private var socket: Socket? = null
    private lateinit var messageCrypt: MessageCrypt
    private lateinit var privateKey: String
    private lateinit var connectionId: String
    private val webRtc = WebRtc()

    private val shutdownRunnable = Runnable { stopSelf() }
    private val timeoutRunnable = Runnable {
        MewLog.d(TAG, "Timeout")
        disconnect()
        errorListener?.invoke()
    }

    var errorListener: (() -> Unit)? = null
    var connectedListener: (() -> Unit)? = null
    var connectingListener: (() -> Unit)? = null
    var transactionConfirmListener: ((transaction: Transaction) -> Unit)? = null
    var messageSignListener: ((message: MessageToSign) -> Unit)? = null
    var disconnectListener: (() -> Unit)? = null


    override fun onCreate() {
        super.onCreate()
        (application as MewApplication).appComponent.inject(this)
    }

    override fun onBind(intent: Intent) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.hasExtra(EXTRA_SHUTDOWN_DELAYED)) {
                handler.postDelayed(shutdownRunnable, SHUTDOWN_DELAY)
            } else {
                handler.removeCallbacks(shutdownRunnable)
            }
        }
        return START_STICKY
    }

    fun connect(privateKey: String, connectionId: String) {
        connectingListener?.invoke()
        this.privateKey = privateKey
        this.connectionId = connectionId
        messageCrypt = MessageCrypt(privateKey)
        val opt = IO.Options()
        opt.secure = true
        socket = IO.socket(BuildConfig.CONNECT_API_END_POINT + "?connId=" + connectionId + "&signed=" + messageCrypt.signMessage(privateKey) + "&stage=receiver", opt)
        socket
                ?.on(Socket.EVENT_CONNECT, ::onConnected)
                ?.on(EVENT_HANDSHAKE, ::onHandShake)
                ?.on(EVENT_ANSWER, ::onAnswer)
                ?.on(EVENT_OFFER, ::onOffer)
                ?.on(EVENT_ERROR, ::onError)
                ?.on(EVENT_CONFIRMATION_FAILED, ::onConfirmationFailed)
                ?.on(EVENT_INVALID_CONNECTION, ::onInvalidConnection)
                ?.on(Socket.EVENT_DISCONNECT, ::onDisconnected)
        socket?.connect()

        handler.postDelayed(timeoutRunnable, CONNECT_TIMEOUT)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onConnected(vararg args: Any) {
        MewLog.d(TAG, "onConnected")
    }

    private fun onOffer(vararg args: Any) {
        MewLog.d(TAG, "onOffer")
        val encryptedMessage = JsonParser.fromJson(args[0] as JSONObject, OfferData::class.java).data
        val offer = JsonParser.fromJson(messageCrypt.decrypt(encryptedMessage)!!, Offer::class.java)

        webRtc.connectSuccessListener = ::onWebRtcConnectSuccess
        webRtc.connectErrorListener = ::onWebRtcConnectError
        webRtc.disconnectListener = ::onRtcDisconnected
        webRtc.dataListener = ::onRtcDataOpened
        webRtc.messageListener = { handleWebRtcMessages(webRtc, it) }
        webRtc.connectWithOffer(this, offer)
    }

    private fun onWebRtcConnectSuccess(offer: Offer) {
        val encryptedOfferMessage = messageCrypt.encrypt(offer)
        val signatureMessage = SocketMessage(connectionId, encryptedOfferMessage)
        socket?.emit(EMIT_ANSWER_SIGNAL, JsonParser.toJsonObject(signatureMessage))
    }

    private fun onWebRtcConnectError() {
        socket?.emit(EMIT_TRY_TURN, JsonParser.toJsonObject(Cont(connectionId, true)))
    }

    private fun onRtcDisconnected() {
        disconnect()
        disconnectListener?.invoke()
    }

    private fun onRtcDataOpened() {
        socket?.emit(EMIT_RTC_CONNECTED)
    }

    private fun handleWebRtcMessages(webRtc: WebRtc, json: String) {
        try {
            val encryptedMessage = JsonParser.fromJson(json, EncryptedMessage::class.java)
            val webRtcMessage = JsonParser.fromJson<WebRtcMessage<JsonElement>>(messageCrypt.decrypt(encryptedMessage)!!, object : TypeToken<WebRtcMessage<JsonElement>>() {}.type)
            when {
                webRtcMessage.type == WebRtcMessage.Type.ADDRESS -> {
                    val message = messageCrypt.encrypt(WebRtcMessage(WebRtcMessage.Type.ADDRESS, Address(preferences.getCurrentWalletPreferences().getWalletAddress())))
                    webRtc.send(message)
                    connectedListener?.invoke()
                    isConnected = true
                    handler.removeCallbacks(timeoutRunnable)
                }
                webRtcMessage.type == WebRtcMessage.Type.SIGN_TX -> {
                    val transaction = JsonParser.fromJson(webRtcMessage.data.asString as String, Transaction::class.java)
                    transactionConfirmListener?.invoke(transaction)
                }
                webRtcMessage.type == WebRtcMessage.Type.SIGN_MESSAGE -> {
                    messageSignListener?.invoke(JsonParser.fromJson(webRtcMessage.data, MessageToSign::class.java))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendSignTx(signedMessage: ByteArray) {
        try {
            val message = WebRtcMessage(WebRtcMessage.Type.SIGN_TX, HexUtils.bytesToStringLowercase(signedMessage))
            webRtc.send(messageCrypt.encrypt(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(address: String, signature: String) {
        try {
            val message = WebRtcMessage(WebRtcMessage.Type.SIGN_MESSAGE, MessageSignData(address, signature))
            webRtc.send(messageCrypt.encrypt(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onError(vararg args: Any) {
        MewLog.d(TAG, "onError")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onInvalidConnection(vararg args: Any) {
        MewLog.d(TAG, "onInvalidConnection")
        disconnect()
        errorListener?.invoke()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onConfirmationFailed(vararg args: Any) {
        MewLog.d(TAG, "onConfirmationFailed")
        errorListener?.invoke()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAnswer(vararg args: Any) {
        MewLog.d(TAG, "onAnswer")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onHandShake(vararg args: Any) {
        MewLog.d(TAG, "onHandShake")
        val signed = messageCrypt.signMessage(privateKey)
        val version = messageCrypt.encrypt(VERSION.toByteArray())
        val signatureMessage = SocketMessage(connectionId, signed, version)
        socket?.emit(EMIT_SIGNATURE, JsonParser.toJsonObject(signatureMessage))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDisconnected(vararg args: Any) {
        MewLog.d(TAG, "onDisconnected")
    }


    fun disconnect() {
        isConnected = false
        try {
            handler.removeCallbacks(timeoutRunnable)
            webRtc.disconnect()
            socket?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        socket?.disconnect()
        super.onDestroy()
    }
}
