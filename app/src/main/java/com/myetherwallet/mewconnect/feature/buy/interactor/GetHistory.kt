package com.myetherwallet.mewconnect.feature.buy.interactor

import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem
import com.myetherwallet.mewconnect.feature.buy.database.BuyHistoryDao
import javax.inject.Inject

/**
 * Created by BArtWell on 17.09.2018.
 */

class GetHistory
@Inject constructor(private val buyHistoryDao: BuyHistoryDao) : BaseInteractor<List<BuyHistoryItem>, GetHistory.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<BuyHistoryItem>> {
        return Either.Right(buyHistoryDao.getAll())
    }

    class Params
}
