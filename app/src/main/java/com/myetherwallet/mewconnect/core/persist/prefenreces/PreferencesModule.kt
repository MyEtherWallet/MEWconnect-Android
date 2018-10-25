package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 10.07.2018.
 */
@Module
class PreferencesModule @Inject constructor(context: Context) {

    private val preferencesManager: PreferencesManager = PreferencesManager(context)

    @Singleton
    @Provides
    fun providePreferences() = preferencesManager
}
