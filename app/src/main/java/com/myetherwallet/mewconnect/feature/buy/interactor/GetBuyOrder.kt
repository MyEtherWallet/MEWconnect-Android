package com.myetherwallet.mewconnect.feature.buy.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.ApiccswapApiRepository
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.buy.data.*
import java.util.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.09.2018.
 */

class GetBuyOrder
@Inject constructor(private val repository: ApiccswapApiRepository) : BaseInteractor<BuyResponse<Map<String, String>>, GetBuyOrder.Params>() {

    override suspend fun run(params: Params): Either<Failure, BuyResponse<Map<String, String>>> {
        val request = BuyOrderRequest(
                BuyOrderAccountDetails(params.quote.userId, params.installTime),
                BuyOrderTransactionDetails(BuyOrderPaymentDetails(
                        Amount("USD", params.quote.fiatMoney.baseAmount),
                        Amount("ETH", params.quote.digitalMoney.amount),
                        BuyOrderPaymentDetailsAddress("ETH", HexUtils.withPrefix(params.address))
                ))
        )
        return repository.getBuyOrder(request)
    }

    data class Params(val quote: BuyQuoteResult, val address: String, val installTime: Date)
}
