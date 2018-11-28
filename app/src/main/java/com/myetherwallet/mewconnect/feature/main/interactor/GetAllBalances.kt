package com.myetherwallet.mewconnect.feature.main.interactor

import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.MewApiRepository
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.core.methods.request.Transaction
import java.math.BigInteger
import java.util.*
import javax.inject.Inject

/**
 * Created by BArtWell on 29.08.2018.
 */

private const val CONTRACT_ADDRESS = "0xdAFf2b3BdC710EB33A847CCb30A24789c0Ef9c5b"
private const val TRANSACTION_METHOD = "eth_call"
private const val FUNCTION_METHOD = "getAllBalance"
private const val NAME = true
private const val WEBSITE = false
private const val EMAIL = false
private val COUNT = BigInteger.ZERO

class GetAllBalances
@Inject constructor(private val repository: MewApiRepository) : BaseInteractor<List<Balance>, GetAllBalances.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Balance>> {
        val function = Function(FUNCTION_METHOD,
                Arrays.asList(Address(HexUtils.withPrefix(params.address)),
                        Bool(NAME),
                        Bool(WEBSITE),
                        Bool(EMAIL),
                        Uint256(COUNT)),
                Arrays.asList<TypeReference<*>>(object : TypeReference<DynamicBytes>() {}))
        val data = FunctionEncoder.encode(function)
        val transaction = Transaction.createEthCallTransaction(null, CONTRACT_ADDRESS, data)
        return repository.getAllBalances(params.network.apiMethod, JsonRpcRequest(TRANSACTION_METHOD, listOf(transaction)))
    }

    data class Params(val network: Network, val address: String)
}
