package com.myetherwallet.mewconnect.feature.scan.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

private const val DELIMITER = "_"

class ScanViewModel
@Inject constructor(application: Application) : AndroidViewModel(application) {

    private var serviceConnection: ServiceConnection
    private var service: SocketService? = null

    init {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                service = (binder as ServiceBinder<SocketService>).service
            }

            override fun onServiceDisconnected(name: ComponentName) {
                service = null
            }
        }
        application.bindService(SocketService.getIntent(application.applicationContext), serviceConnection, 0)
    }

    fun connectWithBarcode(data: String, onStateChangedListener: (state: State) -> Unit) {
        val (_, privateKey, connectionId) = data.split(DELIMITER)
        service?.apply {
            connectingListener = { onStateChangedListener(State.CONNECTING) }
            connectedListener = { onStateChangedListener(State.CONNECTED) }
            errorListener = { onStateChangedListener(State.ERROR) }
            connect(privateKey, connectionId)
        }
    }

    override fun onCleared() {
        getApplication<Application>().unbindService(serviceConnection)
        super.onCleared()
    }

    enum class State {
        CONNECTED, CONNECTING, ERROR
    }
}