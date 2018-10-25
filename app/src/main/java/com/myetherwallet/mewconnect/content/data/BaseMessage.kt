package com.myetherwallet.mewconnect.content.data

import com.myetherwallet.mewconnect.content.gson.JsonParser

/**
 * Created by BArtWell on 29.07.2018.
 */
open class BaseMessage {
    fun toByteArray() = JsonParser.toJson(this).toByteArray()
}