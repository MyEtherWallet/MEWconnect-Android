package com.myetherwallet.mewconnect.feature.scan.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.google.android.gms.common.util.Hex
import com.myetherwallet.mewconnect.content.data.MessageToSign
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.core.utils.crypto.MessageCrypt
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Sign
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

class SignMessageViewModel
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

    fun signMessage(messageToSign: MessageToSign, preferences: PreferencesManager, password: String) {
        if (HexUtils.bytesToStringLowercase(MessageCrypt.formatKeyWithPrefix(messageToSign.text)) != messageToSign.hash) {
            return
        }

        val privateKey = StorageCryptHelper.decrypt(preferences.getCurrentWalletPreferences().getWalletPrivateKey(), password)
        val signatureData = Sign.signMessage(Hex.stringToBytes(messageToSign.hash), ECKeyPair.create(privateKey), false)
        val v = (signatureData.v - 27).toString(16).padStart(2, '0')
        val signature = HexUtils.withPrefix(HexUtils.bytesToStringLowercase(signatureData.r) + HexUtils.bytesToStringLowercase(signatureData.s) + v)

        val address = HexUtils.withPrefix(preferences.getCurrentWalletPreferences().getWalletAddress())
        service?.sendMessage(address, signature)
    }
}