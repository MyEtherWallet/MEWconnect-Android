package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by BArtWell on 30.08.2018.
 */
class JsonRpcRequest<T>(
        @SerializedName("method")
        private val method: String,
        @SerializedName("params")
        private val params: List<T>) {

    companion object {
        private val nextId = AtomicInteger(0)
    }

    @SerializedName("jsonrpc")
    val version: String = "2.0"
    @SerializedName("id")
    val id = nextId.getAndIncrement()
}