package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcResponse
import com.myetherwallet.mewconnect.feature.main.data.WalletBalance
import org.web3j.protocol.core.methods.request.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by BArtWell on 16.07.2018.
 */

internal interface MewApi {

    @POST("eth")
    @Headers("content-type: application/json")
    fun getAllBalances(@Body jsonRpc: JsonRpcRequest<Transaction>): Call<JsonRpcResponse>

    @POST("eth")
    @Headers("content-type: application/json")
    fun getWalletBalance(@Body jsonRpc: JsonRpcRequest<String>): Call<JsonRpcResponse>
}
