package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.content.api.heroku.HerokuApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class HerokuApiService
@Inject constructor(client: HerokuClient) : HerokuApi {

    private val herokuApi by lazy { client.retrofit.create(HerokuApi::class.java) }

    override fun getTickerData(filter: String) = herokuApi.getTickerData(filter)
}
