package com.myetherwallet.mewconnect.feature.buy.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteResult
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

    fun load(amount: BigDecimal, currency:  String, address: String, installTime: Date, successCallback: (postRequest: PostRequest) -> Unit, failureCallback: () -> Unit) {
        getBuyInfo.execute(GetBuyQuote.Params(amount, currency)) {
            it.either(
                    { failureCallback() },
                    {
                        loadOrder(it.result, address, installTime, successCallback, failureCallback)
                    })
        }
    }

    private fun loadOrder(quote: BuyQuoteResult, address: String, installTime: Date, successCallback: (postRequest: PostRequest) -> Unit, failureCallback: () -> Unit) {
        getBuyOrder.execute(GetBuyOrder.Params(quote, address, installTime)) {
            it.either(
                    { failureCallback() },
                    { successCallback(createPostRequest(it.result)) })
        }
        saveHistoryItem.execute(SaveHistoryItem.Params(quote)) {}
        getHistory.execute(GetHistory.Params()) {}
    }

    private fun createPostRequest(data: Map<String, String>): PostRequest {
        val url = data[KEY_URL]!!
        val postData = mutableMapOf<String, String>()
        postData.put("version", data.getValue("version"))
        postData.put("partner", data.getValue("partner"))
        postData.put("payment_flow_type", "wallet")
        postData.put("return_url", data.getValue("return_url"))
        postData.put("quote_id", data.getValue("quote_id"))
        postData.put("payment_id", data.getValue("payment_id"))
        postData.put("user_id", data.getValue("user_id"))
        postData.put("destination_wallet[address]", data.getValue("destination_wallet_address"))
        postData.put("destination_wallet[currency]", data.getValue("destination_wallet_currency"))
        postData.put("fiat_total_amount[amount]", data.getValue("fiat_total_amount_amount"))
        postData.put("fiat_total_amount[currency]", data.getValue("fiat_total_amount_currency"))
        postData.put("digital_total_amount[amount]", data.getValue("digital_total_amount_amount"))
        postData.put("digital_total_amount[currency]", data.getValue("digital_total_amount_currency"))
        return PostRequest(url, postData)
    }
}