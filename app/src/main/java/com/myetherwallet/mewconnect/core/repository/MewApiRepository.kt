package com.myetherwallet.mewconnect.core.repository

import com.myetherwallet.mewconnect.content.api.mew.MewApiService
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.platform.NetworkHandler
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcRequest
import com.myetherwallet.mewconnect.feature.main.utils.JsonRpcResponseConverter
import org.web3j.protocol.core.methods.request.Transaction
import retrofit2.Call
import retrofit2.HttpException
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

interface MewApiRepository {

    fun getAllBalances(apiMethod: String, jsonRpc: JsonRpcRequest<Transaction>): Either<Failure, List<Balance>>

    fun getWalletBalance(apiMethod: String, jsonRpc: JsonRpcRequest<String>): Either<Failure, BigDecimal>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: MewApiService) : MewApiRepository {

        override fun getAllBalances(apiMethod: String, jsonRpc: JsonRpcRequest<Transaction>): Either<Failure, List<Balance>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getAllBalances(apiMethod, jsonRpc)) { JsonRpcResponseConverter(it).toBalancesList() }
                false, null -> Either.Left(Failure.NetworkConnection())
            }
        }

        override fun getWalletBalance(apiMethod: String, jsonRpc: JsonRpcRequest<String>): Either<Failure, BigDecimal> {
            return when (networkHandler.isConnected) {
                true -> request(service.getWalletBalance(apiMethod, jsonRpc)) { JsonRpcResponseConverter(it).toWalletBalance() }
                false, null -> Either.Left(Failure.NetworkConnection())
            }
        }

        private fun <T, R> request(call: Call<T>, transform: (T) -> R): Either<Failure, R> {
            return try {
                val response = call.execute()
                when (response.isSuccessful) {
                    true -> {
                        val body = response.body()
                        if (body == null) {
                            Either.Left(Failure.ServerError(IllegalStateException("Body is empty")))
                        } else {
                            Either.Right(transform(body))
                        }
                    }
                    false -> Either.Left(Failure.ServerError(HttpException(response)))
                }
            } catch (exception: Throwable) {
                exception.printStackTrace()
                Either.Left(Failure.UnknownError(exception))
            }
        }
    }
}
