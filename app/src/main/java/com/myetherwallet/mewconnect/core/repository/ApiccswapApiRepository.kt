package com.myetherwallet.mewconnect.core.repository

import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.content.api.apiccswap.ApiccswapApiService
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.platform.NetworkHandler
import com.myetherwallet.mewconnect.feature.buy.data.*
import retrofit2.Call
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by BArtWell on 15.09.2018.
 */

interface ApiccswapApiRepository {

    fun getBuyOrder(request: BuyOrderRequest): Either<Failure, BuyResponse<Map<String, String>>>

    fun getBuyQuote(request: BuyQuoteRequest): Either<Failure, BuyResponse<BuyQuoteResult>>

    fun getStatus(userId: String): Either<Failure, BuyResponse<PurchaseStatus>>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: ApiccswapApiService) : ApiccswapApiRepository {

        override fun getBuyOrder(request: BuyOrderRequest): Either<Failure, BuyResponse<Map<String, String>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBuyOrder(BuildConfig.APICCSWAP_API_KEY, BuildConfig.APICCSWAP_REFERER, request)) { it }
                false, null -> Either.Left(Failure.NetworkConnection())
            }
        }

        override fun getBuyQuote(request: BuyQuoteRequest): Either<Failure, BuyResponse<BuyQuoteResult>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getBuyQuote(BuildConfig.APICCSWAP_API_KEY, BuildConfig.APICCSWAP_REFERER, request)) { it }
                false, null -> Either.Left(Failure.NetworkConnection())
            }
        }

        override fun getStatus(userId: String): Either<Failure, BuyResponse<PurchaseStatus>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getStatus(userId)) { it }
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
