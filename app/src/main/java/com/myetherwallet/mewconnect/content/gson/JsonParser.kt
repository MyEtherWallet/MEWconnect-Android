package com.myetherwallet.mewconnect.content.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.myetherwallet.mewconnect.content.data.EncryptedMessage
import org.json.JSONObject
import java.lang.reflect.Type
import java.math.BigInteger

/**
 * Created by BArtWell on 29.07.2018.
 */
object JsonParser {

    private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(BigInteger::class.java, BigIntegerSerializer())
            .registerTypeAdapter(ByteArray::class.java, ByteArraySerializer())
            .registerTypeAdapter(EncryptedMessage::class.java, EncryptedMessageSerializer())
            .create()

    fun <T> fromJson(json: ByteArray, c: Class<T>): T = fromJson(String(json), c)

    fun <T> fromJson(json: ByteArray, type: Type): T = fromJson(String(json), type)

    fun <T> fromJson(json: String, type: Type): T = gson.fromJson(json, type)

    fun <T> fromJson(json: String, c: Class<T>): T = gson.fromJson(json, c)

    fun <T> fromJson(json: JsonElement, c: Class<T>): T = gson.fromJson(json, c)

    fun <T> fromJson(json: JSONObject, c: Class<T>): T = gson.fromJson(json.toString(), c)

    fun <T> toJson(data: T) = gson.toJson(data)

    fun <T> toJsonObject(data: T) = JSONObject(toJson(data))
}