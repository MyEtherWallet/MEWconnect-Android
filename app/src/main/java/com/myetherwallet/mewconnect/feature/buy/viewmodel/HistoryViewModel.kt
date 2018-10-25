package com.myetherwallet.mewconnect.feature.buy.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem
import com.myetherwallet.mewconnect.feature.buy.data.BuyResponse
import com.myetherwallet.mewconnect.feature.buy.data.PurchaseStatus
import com.myetherwallet.mewconnect.feature.buy.interactor.GetHistory
import com.myetherwallet.mewconnect.feature.buy.interactor.GetStatus
import javax.inject.Inject

/**
 * Created by BArtWell on 18.09.2018.
 */

class HistoryViewModel
@Inject constructor(application: Application, private val getHistory: GetHistory, private val getStatus: GetStatus) : AndroidViewModel(application) {

    var data: MutableLiveData<List<PurchaseStatus>> = MutableLiveData()
    private val statuses = mutableListOf<PurchaseStatus>()
    private var count = 0
    private var loaded = 0

    fun load() {
        getHistory.execute(GetHistory.Params()) {
            it.either({}, ::getStatuses)
        }
    }

    private fun getStatuses(items: List<BuyHistoryItem>) {
        count = items.size
        loaded = 0
        if (items.isEmpty()) {
            data.postValue(emptyList())
        } else {
            for (item in items) {
                getStatus.execute(GetStatus.Params(item.userId)) {
                    it.either(::onStatusLoadFailed, ::onStatusLoaded)
                }
            }
        }
    }

    private fun onStatusLoaded(response: BuyResponse<PurchaseStatus>) {
        loaded++
        statuses.add(response.result)
        if (loaded >= count) {
            data.postValue(statuses.filter { it.status != "unknown" })
        }
    }

    private fun onStatusLoadFailed(failure: Failure) {
        loaded++
    }
}