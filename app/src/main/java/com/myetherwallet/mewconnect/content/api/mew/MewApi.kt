package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.content.data.AnalyticsEventsRequest
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by BArtWell on 26.03.2020.
 */

internal interface MewApi {

    @POST("analytics/record/{platform}")
    @Headers("content-type: application/json")
    fun submit(@Path("platform") platform: String, @Query("iso") iso: String, @Body events: AnalyticsEventsRequest): Call<Any>
}
