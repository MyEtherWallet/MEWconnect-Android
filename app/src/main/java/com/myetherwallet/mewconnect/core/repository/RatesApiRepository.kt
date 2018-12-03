package com.myetherwallet.mewconnect.core.repository

import com.myetherwallet.mewconnect.content.api.rates.RatesApiService
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.platform.NetworkHandler
import retrofit2.Call
import retrofit2.HttpException
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 16.07.2018.
 */

interface RatesApiRepository {

    fun getTickerData(filter: String): Either<Failure, Map<String, BigDecimal>>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: RatesApiService) : RatesApiRepository {

        override fun getTickerData(filter: String): Either<Failure, Map<String, BigDecimal>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getTickerData(filter)) {
                    val result = mutableMapOf<String, BigDecimal>()
                    for (entry in it.data) {
                        result.put(entry.value.symbol, entry.value.quotes.usd.price)
                    }
                    result
                }
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
