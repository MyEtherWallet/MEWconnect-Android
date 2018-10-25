package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import org.web3j.protocol.core.methods.request.Transaction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class MewApiService
@Inject constructor(client: MewClient) : MewApi {

    private val mewApi by lazy { client.retrofit.create(MewApi::class.java) }

    override fun getAllBalances(jsonRpc: JsonRpcRequest<Transaction>) = mewApi.getAllBalances(jsonRpc)

    override fun getWalletBalance(jsonRpc: JsonRpcRequest<String>) = mewApi.getWalletBalance(jsonRpc)
}
