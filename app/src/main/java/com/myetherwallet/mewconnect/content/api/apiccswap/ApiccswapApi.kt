package com.myetherwallet.mewconnect.content.api.apiccswap

import com.myetherwallet.mewconnect.feature.buy.data.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by BArtWell on 15.09.2018.
 */

internal interface ApiccswapApi {

    @Headers("Content-Type: application/json",
            "Accept: application/json")
    @POST("order")
    fun getBuyOrder(@Header("mewapikey") key: String, @Header("Referer") referer: String, @Body request: BuyOrderRequest): Call<BuyResponse<Map<String, String>>>

    @Headers("Content-Type: application/json",
            "Accept: application/json")
    @POST("quote")
    fun getBuyQuote(@Header("mewapikey") key: String, @Header("Referer") referer: String, @Body request: BuyQuoteRequest): Call<BuyResponse<BuyQuoteResult>>

    @GET("status/{userId}")
    fun getStatus(@Path("userId") userId: String): Call<BuyResponse<PurchaseStatus>>
}
