package com.myetherwallet.mewconnect.feature.scan.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import org.web3j.crypto.Credentials
import org.web3j.crypto.TransactionEncoder
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

class ConfirmTransactionViewModel
@Inject constructor(application: Application) : AndroidViewModel(application) {

    private var serviceConnection: ServiceConnection
    private var service: SocketService? = null
    lateinit var transaction: Transaction

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

    override fun onCleared() {
        getApplication<Application>().unbindService(serviceConnection);
        super.onCleared()
    }

    fun confirmTransaction(preferences: PreferencesManager, password: String) {
        val privateKey = StorageCryptHelper.decrypt(preferences.getCurrentWalletPreferences().getWalletPrivateKey(), password)
        val chainId = preferences.applicationPreferences.getCurrentNetwork().chainId
        val credentials = Credentials.create(HexUtils.bytesToStringLowercase(privateKey))
        val rawTransaction = transaction.toRawTransaction()
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials)
        service?.sendSignTx(signedMessage)
    }
}