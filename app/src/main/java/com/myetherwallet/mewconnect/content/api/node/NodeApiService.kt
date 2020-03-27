package com.myetherwallet.mewconnect.content.api.node

import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class NodeApiService
@Inject constructor(client: NodeClient) : NodeApi {

    private val mewApi by lazy { client.retrofit.create(NodeApi::class.java) }

    override fun getAllBalances(apiMethod: String, jsonRpc: JsonRpcRequest<Any>) = mewApi.getAllBalances(apiMethod, jsonRpc)

    override fun getWalletBalance(apiMethod: String, jsonRpc: JsonRpcRequest<String>) = mewApi.getWalletBalance(apiMethod, jsonRpc)
}
