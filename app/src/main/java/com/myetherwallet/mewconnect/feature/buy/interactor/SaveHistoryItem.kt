package com.myetherwallet.mewconnect.feature.buy.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteResult
import com.myetherwallet.mewconnect.feature.buy.database.BuyHistoryDao
import javax.inject.Inject

/**
 * Created by BArtWell on 17.09.2018.
 */

class SaveHistoryItem
@Inject constructor(private val buyHistoryDao: BuyHistoryDao) : BaseInteractor<Any, SaveHistoryItem.Params>() {

    override suspend fun run(params: Params): Either<Failure, Any> {
        buyHistoryDao.insert(BuyHistoryItem(params.quote.userId))
        return Either.Right(Any())
    }

    data class Params(val quote: BuyQuoteResult)
}
