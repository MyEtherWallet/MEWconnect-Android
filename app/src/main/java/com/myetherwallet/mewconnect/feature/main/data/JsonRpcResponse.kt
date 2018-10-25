package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 31.08.2018.
 */

class JsonRpcResponse(
        @SerializedName("result")
        var result: String
)