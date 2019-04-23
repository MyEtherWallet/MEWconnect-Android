package com.myetherwallet.mewconnect.feature.buy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteResult
import com.myetherwallet.mewconnect.feature.buy.data.BuyResponse
import com.myetherwallet.mewconnect.feature.buy.data.PostRequest
import com.myetherwallet.mewconnect.feature.buy.interactor.GetBuyOrder
import com.myetherwallet.mewconnect.feature.buy.interactor.GetBuyQuote
import com.myetherwallet.mewconnect.feature.buy.interactor.GetHistory
import com.myetherwallet.mewconnect.feature.buy.interactor.SaveHistoryItem
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.09.2018.
 */

private const val KEY_URL = "payment_post_url"

class BuyViewModel
@Inject constructor(application: Application, private val getBuyInfo: GetBuyQuote, private val getBuyOrder: GetBuyOrder, private val saveHistoryItem: SaveHistoryItem, private val getHistory: GetHistory) : AndroidViewModel(application) {

    var data: MutableLiveData<BuyResponse<BuyQuoteResult>> = MutableLiveData()

    fun loadQuote() {
        getBuyInfo.execute(GetBuyQuote.Params(BigDecimal.ONE, "ETH")) {
            it.either({ }, { data.postValue(it) })
        }
    }

    fun preparePostRequest(amount: BigDecimal, currency: String, address: String, installTime: Date, successCallback: (postRequest: PostRequest) -> Unit, failureCallback: () -> Unit) {
        getBuyInfo.execute(GetBuyQuote.Params(amount, currency)) {
            it.either(
                    { failureCallback() },
                    {
                        loadOrder(it.result, address, installTime, successCallback, failureCallback)
                    })
        }
    }

    private fun loadOrder(quote: BuyQuoteResult?, address: String, installTime: Date, successCallback: (postRequest: PostRequest) -> Unit, failureCallback: () -> Unit) {
        if (quote?.userId != null) {
            getBuyOrder.execute(GetBuyOrder.Params(quote, address, installTime)) {
                it.either(
                        { failureCallback() },
                        {
                            try {
                                successCallback(createPostRequest(it.result))
                            } catch (e: Exception) {
                                e.printStackTrace()
                                failureCallback()
                            }
                        })
            }
            saveHistoryItem.execute(SaveHistoryItem.Params(quote)) {}
            getHistory.execute(GetHistory.Params()) {}
        } else {
            failureCallback()
        }
    }

    private fun createPostRequest(data: Map<String, String>): PostRequest {
        val url = data.getValue(KEY_URL)
        val postData = mutableMapOf<String, String>()
        postData["version"] = data.getValue("version")
        postData["partner"] = data.getValue("partner")
        postData["payment_flow_type"] = "wallet"
        postData["return_url"] = data.getValue("return_url")
        postData["quote_id"] = data.getValue("quote_id")
        postData["payment_id"] = data.getValue("payment_id")
        postData["user_id"] = data.getValue("user_id")
        postData["destination_wallet[address]"] = data.getValue("destination_wallet_address")
        postData["destination_wallet[currency]"] = data.getValue("destination_wallet_currency")
        postData["fiat_total_amount[amount]"] = data.getValue("fiat_total_amount_amount")
        postData["fiat_total_amount[currency]"] = data.getValue("fiat_total_amount_currency")
        postData["digital_total_amount[amount]"] = data.getValue("digital_total_amount_amount")
        postData["digital_total_amount[currency]"] = data.getValue("digital_total_amount_currency")
        return PostRequest(url, postData)
    }
}