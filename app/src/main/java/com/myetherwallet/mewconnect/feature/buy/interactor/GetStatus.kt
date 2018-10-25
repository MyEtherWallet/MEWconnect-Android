package com.myetherwallet.mewconnect.feature.buy.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.ApiccswapApiRepository
import com.myetherwallet.mewconnect.feature.buy.data.BuyResponse
import com.myetherwallet.mewconnect.feature.buy.data.PurchaseStatus
import javax.inject.Inject

/**
 * Created by BArtWell on 18.09.2018.
 */

class GetStatus
@Inject constructor(private val repository: ApiccswapApiRepository) : BaseInteractor<BuyResponse<PurchaseStatus>, GetStatus.Params>() {

    override suspend fun run(params: Params): Either<Failure, BuyResponse<PurchaseStatus>> {
        return repository.getStatus(params.userId)
    }

    data class Params(val userId: String)
}
