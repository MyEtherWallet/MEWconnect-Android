package com.myetherwallet.mewconnect.feature.main.interactor

import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.MewApiRepository
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 02.09.2018.
 */

private const val METHOD = "eth_getBalance"
private const val PERIOD = "latest"

class GetWalletBalance
@Inject constructor(private val repository: MewApiRepository) : BaseInteractor<BigDecimal, GetWalletBalance.Params>() {

    override suspend fun run(params: Params): Either<Failure, BigDecimal> {
        val address = HexUtils.withPrefix(params.address)
        val request = JsonRpcRequest(METHOD, listOf(address, PERIOD))
        return repository.getWalletBalance(params.network.apiMethod, request)
    }

    data class Params(val network: Network, val address: String)
}
