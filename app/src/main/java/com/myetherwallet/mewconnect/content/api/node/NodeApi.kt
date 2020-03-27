package com.myetherwallet.mewconnect.content.api.node

import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by BArtWell on 16.07.2018.
 */

internal interface NodeApi {

    @POST("{apiMethod}")
    @Headers("content-type: application/json")
    fun getAllBalances(@Path("apiMethod") apiMethod: String, @Body jsonRpc: JsonRpcRequest<Any>): Call<JsonRpcResponse>

    @POST("{apiMethod}")
    @Headers("content-type: application/json")
    fun getWalletBalance(@Path("apiMethod") apiMethod: String, @Body jsonRpc: JsonRpcRequest<String>): Call<JsonRpcResponse>
}
