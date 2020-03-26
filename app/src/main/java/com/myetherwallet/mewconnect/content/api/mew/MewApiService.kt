package com.myetherwallet.mewconnect.content.api.mew

import com.myetherwallet.mewconnect.content.data.AnalyticsEventsRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class MewApiService
@Inject constructor(client: MewClient) : MewApi {

    private val mewApi by lazy { client.retrofit.create(MewApi::class.java) }

    override fun submit(platform: String, iso: String, events: AnalyticsEventsRequest) = mewApi.submit(platform, iso, events)
}
