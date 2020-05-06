package com.myetherwallet.mewconnect.feature.main.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.content.data.AnalyticsEvent
import com.myetherwallet.mewconnect.content.data.MessageToSign
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.core.persist.prefenreces.WalletPreferences
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.WalletBalance
import com.myetherwallet.mewconnect.feature.main.data.WalletData
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem
import com.myetherwallet.mewconnect.feature.main.interactor.GetAllBalances
import com.myetherwallet.mewconnect.feature.main.interactor.GetTickerData
import com.myetherwallet.mewconnect.feature.main.interactor.GetWalletBalance
import com.myetherwallet.mewconnect.feature.main.interactor.SubmitAnalyticsEvent
import com.myetherwallet.mewconnect.feature.scan.service.ServiceBinder
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 28.04.2020.
 */

class IntroViewModel
@Inject constructor(application: Application, private val submitAnalyticsEvent: SubmitAnalyticsEvent) : AndroidViewModel(application) {

    fun submitEvents(context: Context?, event: AnalyticsEvent) {
        context?.let {
            submitAnalyticsEvent.execute(SubmitAnalyticsEvent.Params(ApplicationUtils.getCountryIso(context), listOf(event))) {
                it.either({ }, { })
            }
        }
    }
}
