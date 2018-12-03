package com.myetherwallet.mewconnect.content.api.rates

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class RatesApiService
@Inject constructor(client: RatesClient) : RatesApi {

    private val ratesApi by lazy { client.retrofit.create(RatesApi::class.java) }

    override fun getTickerData(filter: String) = ratesApi.getTickerData(filter)
}
