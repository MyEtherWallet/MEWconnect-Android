package com.myetherwallet.mewconnect

import android.app.Application
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.di.ApplicationModule
import com.myetherwallet.mewconnect.core.di.DaggerApplicationComponent
import com.myetherwallet.mewconnect.core.persist.database.DatabaseModule
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesModule
import javax.inject.Inject


class MewApplication : Application() {

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .preferencesModule(PreferencesModule(this))
                .databaseModule(DatabaseModule(this))
                .build()
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        injectMembers()

        preferences.applicationPreferences.setInstallTime()
    }

    private fun injectMembers() = appComponent.inject(this)
}