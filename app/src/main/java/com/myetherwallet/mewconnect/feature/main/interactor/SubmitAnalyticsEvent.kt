package com.myetherwallet.mewconnect.feature.main.interactor

import com.myetherwallet.mewconnect.content.data.AnalyticsEvent
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.repository.MewApiRepository
import javax.inject.Inject

/**
 * Created by BArtWell on 26.03.2020.
 */

class SubmitAnalyticsEvent
@Inject constructor(private val repository: MewApiRepository) : BaseInteractor<Any, SubmitAnalyticsEvent.Params>() {

    override suspend fun run(params: Params): Either<Failure, Any> {
        return repository.submit(params.iso, params.events)
    }

    data class Params(
            val iso: String,
            val events: List<AnalyticsEvent>
    )
}
