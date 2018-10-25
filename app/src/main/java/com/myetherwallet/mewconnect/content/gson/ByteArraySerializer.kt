package com.myetherwallet.mewconnect.content.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Created by BArtWell on 25.07.2018.
 */
class ByteArraySerializer : JsonSerializer<ByteArray>, JsonDeserializer<ByteArray> {
    override fun serialize(src: ByteArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonArray = JsonArray()
        for (byte in src) {
            jsonArray.add(byteToInt(byte))
        }
        return jsonArray
    }

    private fun byteToInt(byte: Byte): Int {
        return byte.toInt() and 0xff
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ByteArray {
        if (json is JsonArray) {
            val byteArray = ByteArray(json.size())
            for (i in 0 until json.size()) {
                byteArray[i] = (json.get(i).asInt).toByte()
            }
            return byteArray
        }
        return ByteArray(0)
    }
}