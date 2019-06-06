package com.myetherwallet.mewconnect.feature.main.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.content.data.MessageToSign
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.core.persist.prefenreces.WalletPreferences
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.WalletBalance
import com.myetherwallet.mewconnect.feature.main.data.WalletData
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem
import com.myetherwallet.mewconnect.feature.main.interactor.GetAllBalances
import com.myetherwallet.mewconnect.feature.main.interactor.GetTickerData
import com.myetherwallet.mewconnect.feature.main.interactor.GetWalletBalance
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 28.08.2018.
 */

private const val USD_SYMBOL = "ETH"

class WalletViewModel
@Inject constructor(application: Application, private val getWalletBalance: GetWalletBalance, private val getAllBalances: GetAllBalances, private val getTickerData: GetTickerData) : AndroidViewModel(application) {

    private var serviceConnection: ServiceConnection? = null
    private var service: SocketService? = null
    private var disconnectListener: (() -> Unit)? = null
    private var messageSignListener: ((message: MessageToSign) -> Unit)? = null
    private var transactionConfirmListener: ((transaction: Transaction) -> Unit)? = null

    var walletData: MutableLiveData<WalletData> = MutableLiveData()

    init {
        bindService()
    }

    fun bindService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                service = (binder as ServiceBinder<SocketService>).service
                service?.disconnectListener = disconnectListener
                service?.messageSignListener = messageSignListener
                service?.transactionConfirmListener = transactionConfirmListener
            }

            override fun onServiceDisconnected(name: ComponentName) {
                service = null
            }
        }
        val context: Context = getApplication()
        context.bindService(SocketService.getIntent(context), serviceConnection, 0)
    }

    fun setOnTransactionListener(onTransactionListener: (transaction: Transaction) -> Unit) {
        transactionConfirmListener = { onTransactionListener(it) }
    }

    fun setOnMessageListener(onMessageListener: (message: MessageToSign) -> Unit) {
        messageSignListener = { onMessageListener(it) }
    }

    fun setOnDisconnectListener(onDisconnectListener: (() -> Unit)?) {
        disconnectListener = onDisconnectListener
    }

    fun checkConnected() = service?.isConnected ?: false

    fun disconnect() {
        SocketService.stop(getApplication())
    }

    override fun onCleared() {
        getApplication<MewApplication>().unbindService(serviceConnection)
        super.onCleared()
    }

    fun loadData(preferences: WalletPreferences, network: Network, walletAddress: String) {
        preferences.getWalletDataCache()?.let {
            it.isFromCache = true
            walletData.postValue(it)
        }
        Collector(network, walletAddress, getWalletBalance, getAllBalances, getTickerData) { items, balance ->
            val data = WalletData(false, items, balance)
            walletData.postValue(data)
            preferences.setWalletDataCache(data)
        }.execute()
    }

    private class Collector(private var network: Network,
                            private var walletAddress: String,
                            private val getWalletBalance: GetWalletBalance,
                            private val getAllBalances: GetAllBalances,
                            private val getTickerData: GetTickerData,
                            private val callback: (List<WalletListItem>, WalletBalance) -> Unit) {

        private var balances: List<Balance>? = null
        private var walletBalance: BigDecimal? = null
        private var tickerData: Map<String, BigDecimal>? = null

        fun execute() {
            loadWalletBalance()
            loadAllBalances()
        }

        fun loadWalletBalance() {
            getWalletBalance.execute(GetWalletBalance.Params(network, walletAddress)) { result ->
                result.either(::onWalletBalanceFail, ::onWalletBalanceSuccess)
            }
        }

        private fun onWalletBalanceFail(failure: Failure) {
            walletBalance = BigDecimal.ZERO
            collect()
        }

        private fun onWalletBalanceSuccess(result: BigDecimal) {
            walletBalance = result
            collect()
        }

        private fun loadAllBalances() {
            getAllBalances.execute(GetAllBalances.Params(network, walletAddress)) {
                it.either(::onAllBalancesFail, ::onAllBalancesSuccess)
            }
        }

        private fun onAllBalancesFail(failure: Failure) {
            balances = emptyList()
            loadTickerData()
        }

        private fun onAllBalancesSuccess(result: List<Balance>) {
            balances = result
            loadTickerData()
        }

        private fun loadTickerData() {
            val symbols = mutableListOf(USD_SYMBOL)
            balances?.let {
                for (balance in it) {
                    symbols.add(balance.symbol)
                }
            }
            getTickerData.execute(GetTickerData.Params(symbols)) {
                it.either(::onTickerDataFail, ::onTickerDataSuccess)
            }
        }

        private fun onTickerDataFail(failure: Failure) {
            tickerData = emptyMap()
            collect()
        }

        private fun onTickerDataSuccess(result: Map<String, BigDecimal>) {
            tickerData = result
            collect()
        }

        private fun collect() {
            if (balances == null || tickerData == null || walletBalance == null) {
                return
            }
            val items = mutableListOf<WalletListItem>()
            for (balance in balances!!) {
                val name = balance.name ?: ""
                val stockPrice = tickerData!![balance.symbol]
                val valueUsd = stockPrice?.multiply(balance.calculateBalance())
                items.add(WalletListItem(name, balance.symbol, balance.calculateBalance(), valueUsd, stockPrice))
            }

            val stockPrice = tickerData!![USD_SYMBOL]
            val valueUsd = stockPrice?.multiply(walletBalance)
            callback(items, WalletBalance(walletBalance!!, valueUsd, stockPrice))
        }
    }
}
