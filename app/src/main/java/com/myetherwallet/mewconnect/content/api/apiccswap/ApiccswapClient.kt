package com.myetherwallet.mewconnect.content.api.apiccswap

import com.google.gson.GsonBuilder
import com.myetherwallet.mewconnect.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Created by BArtWell on 15.09.2018.
 */

class ApiccswapClient {

    val retrofit = createRetrofit()

    private fun createRetrofit(): Retrofit {
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create()
        return Retrofit.Builder()
                .baseUrl(BuildConfig.APICCSWAP_API_END_POINT)
                .client(createClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }
}