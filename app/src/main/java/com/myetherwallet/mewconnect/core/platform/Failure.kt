package com.myetherwallet.mewconnect.core.platform

/**
 * Created by BArtWell on 16.07.2018.
 */

sealed class Failure(val throwable: Throwable?) {
    class NetworkConnection() : Failure(null)
    class ServerError(throwable: Throwable) : Failure(throwable)
    class UnknownError(throwable: Throwable) : Failure(throwable)

    abstract class FeatureFailure(throwable: Throwable) : Failure(throwable)
}
