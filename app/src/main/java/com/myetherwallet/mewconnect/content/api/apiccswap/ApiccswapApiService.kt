package com.myetherwallet.mewconnect.content.api.apiccswap

import com.myetherwallet.mewconnect.feature.buy.data.BuyOrderRequest
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 15.09.2018.
 */

@Singleton
class ApiccswapApiService
@Inject constructor(client: ApiccswapClient) : ApiccswapApi {

    private val apiccswapApi by lazy { client.retrofit.create(ApiccswapApi::class.java) }

    override fun getBuyOrder(key: String, referer: String, request: BuyOrderRequest) = apiccswapApi.getBuyOrder(key, referer, request)

    override fun getBuyQuote(key: String, referer: String, request: BuyQuoteRequest) = apiccswapApi.getBuyQuote(key, referer, request)

    override fun getStatus(userId: String) = apiccswapApi.getStatus(userId)
}
