package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcResponse
import org.web3j.protocol.core.methods.request.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by BArtWell on 16.07.2018.
 */

internal interface MewApi {

    @POST("{apiMethod}")
    @Headers("content-type: application/json")
    fun getAllBalances(@Path("apiMethod") apiMethod: String, @Body jsonRpc: JsonRpcRequest<Transaction>): Call<JsonRpcResponse>

    @POST("{apiMethod}")
    @Headers("content-type: application/json")
    fun getWalletBalance(@Path("apiMethod") apiMethod: String, @Body jsonRpc: JsonRpcRequest<String>): Call<JsonRpcResponse>
}
