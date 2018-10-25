package com.myetherwallet.mewconnect.core.platform

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Created by BArtWell on 16.07.2018.
 */

abstract class BaseInteractor<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    fun execute(params: Params, onResult: (Either<Failure, Type>) -> Unit) {
        val job = async(CommonPool) { run(params) }
        launch(UI) { onResult.invoke(job.await()) }
    }

    class None
}
