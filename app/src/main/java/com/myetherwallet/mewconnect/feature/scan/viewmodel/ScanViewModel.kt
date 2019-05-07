package com.myetherwallet.mewconnect.feature.scan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

private const val DELIMITER = "_"

class ScanViewModel
@Inject constructor(application: Application) : AndroidViewModel(application) {

    private lateinit var serviceConnection: ServiceConnection
    private var service: SocketService? = null
    private var privateKey: String? = null
    private var connectionId: String? = null
    private var onStateChangedListener: ((State) -> Unit?)? = null

    init {
        bindToService(application)
    }

    private fun bindToService(context: Context) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                service = (binder as ServiceBinder<SocketService>).service
                service?.apply {
                    if (privateKey != null && connectionId != null) {
                        connectingListener = { onStateChangedListener?.invoke(State.CONNECTING) }
                        connectedListener = { onStateChangedListener?.invoke(State.CONNECTED) }
                        errorListener = { onStateChangedListener?.invoke(State.ERROR) }
                        connect(privateKey!!, connectionId!!)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                service = null
            }
        }
        context.bindService(SocketService.getIntent(context), serviceConnection, 0)
    }

    fun connectWithBarcode(data: String, listener: (state: State) -> Unit) {
        val parts = data.split(DELIMITER)
        if (parts.size == 3) {
            privateKey = parts[1]
            connectionId = parts[2]
            onStateChangedListener = listener
            SocketService.start(getApplication())
        }
        bindToService(getApplication())
    }

    override fun onCleared() {
        getApplication<Application>().unbindService(serviceConnection)
        super.onCleared()
    }

    enum class State {
        CONNECTED, CONNECTING, ERROR
    }
}