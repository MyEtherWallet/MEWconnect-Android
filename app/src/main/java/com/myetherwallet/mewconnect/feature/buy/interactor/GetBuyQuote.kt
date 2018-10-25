package com.myetherwallet.mewconnect.feature.buy.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.ApiccswapApiRepository
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteRequest
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteResult
import com.myetherwallet.mewconnect.feature.buy.data.BuyResponse
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 15.09.2018.
 */

class GetBuyQuote
@Inject constructor(private val repository: ApiccswapApiRepository) : BaseInteractor<BuyResponse<BuyQuoteResult>, GetBuyQuote.Params>() {

    override suspend fun run(params: Params): Either<Failure, BuyResponse<BuyQuoteResult>> {
        val request = BuyQuoteRequest(params.requestedCurrency, params.amount)
        return repository.getBuyQuote(request)
    }

    data class Params(val amount: BigDecimal, val requestedCurrency: String)
}
