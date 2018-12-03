package com.myetherwallet.mewconnect.content.api.rates

import com.myetherwallet.mewconnect.feature.main.data.TickerData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by BArtWell on 16.07.2018.
 */

internal interface RatesApi {

    @GET("ticker")
    fun getTickerData(@Query("filter") filter: String): Call<TickerData>
}
