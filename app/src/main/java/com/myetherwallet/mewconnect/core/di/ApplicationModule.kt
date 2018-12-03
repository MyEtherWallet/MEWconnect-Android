package com.myetherwallet.mewconnect.core.di

import android.app.Application
import android.content.Context
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.content.api.apiccswap.ApiccswapClient
import com.myetherwallet.mewconnect.content.api.rates.RatesClient
import com.myetherwallet.mewconnect.content.api.mew.MewClient
import com.myetherwallet.mewconnect.core.repository.ApiccswapApiRepository
import com.myetherwallet.mewconnect.core.repository.RatesApiRepository
import com.myetherwallet.mewconnect.core.repository.MewApiRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: MewApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideMewApiClient() = MewClient()

    @Provides
    @Singleton
    fun provideApiccswapApiClient() = ApiccswapClient()

    @Provides
    @Singleton
    fun provideRatesApiClient() = RatesClient()

    @Provides
    @Singleton
    fun provideMewApiRepository(dataSource: MewApiRepository.Network): MewApiRepository = dataSource

    @Provides
    @Singleton
    fun provideApiccswapApiRepository(dataSource: ApiccswapApiRepository.Network): ApiccswapApiRepository = dataSource

    @Provides
    @Singleton
    fun provideRatesApiRepository(dataSource: RatesApiRepository.Network): RatesApiRepository = dataSource
}
