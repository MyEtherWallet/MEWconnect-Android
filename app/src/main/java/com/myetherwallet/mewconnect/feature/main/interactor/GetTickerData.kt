package com.myetherwallet.mewconnect.feature.main.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.RatesApiRepository
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created by BArtWell on 29.08.2018.
 */

class GetTickerData
@Inject constructor(private val repository: RatesApiRepository) : BaseInteractor<Map<String, BigDecimal>, GetTickerData.Params>() {

    override suspend fun run(params: Params): Either<Failure, Map<String, BigDecimal>> {
        return repository.getTickerData(params.filter.joinToString(","))
    }

    data class Params(val filter: List<String>)
}
