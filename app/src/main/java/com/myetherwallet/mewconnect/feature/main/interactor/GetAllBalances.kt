package com.myetherwallet.mewconnect.feature.main.interactor

import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.NodeApiRepository
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.data.Transaction
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigInteger
import java.util.*
import javax.inject.Inject

/**
 * Created by BArtWell on 29.08.2018.
 */

private const val TRANSACTION_METHOD = "eth_call"
private const val FUNCTION_METHOD = "getAllBalance"
private const val NAME = true
private const val WEBSITE = false
private const val EMAIL = false
private val COUNT = BigInteger.ZERO

private const val PERIOD = "latest"

class GetAllBalances
@Inject constructor(private val repository: NodeApiRepository) : BaseInteractor<List<Balance>, GetAllBalances.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Balance>> {
        val function = Function(FUNCTION_METHOD,
                Arrays.asList(Address(HexUtils.withPrefixLowerCase(params.address)),
                        Bool(NAME),
                        Bool(WEBSITE),
                        Bool(EMAIL)),
                Arrays.asList<TypeReference<*>>(object : TypeReference<DynamicBytes>() {}))
        val data = FunctionEncoder.encode(function)
        val transaction = Transaction(HexUtils.withPrefix(params.address), null, null, "0x11e1a300", params.network.contract, null, data)
        return repository.getAllBalances(params.network.apiMethod, JsonRpcRequest(TRANSACTION_METHOD, listOf(transaction, PERIOD)))
    }

    data class Params(val network: Network, val address: String)
}
