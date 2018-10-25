package com.myetherwallet.mewconnect.content.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.myetherwallet.mewconnect.core.utils.HexUtils
import java.lang.reflect.Type
import java.math.BigInteger

/**
 * Created by BArtWell on 28.07.2018.
 */

class BigIntegerSerializer : JsonDeserializer<BigInteger> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BigInteger {
        return HexUtils.toBigInteger(json.asString)
    }
}