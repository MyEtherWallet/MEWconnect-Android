package com.myetherwallet.mewconnect.core.ui.activity

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.CallSuper
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import javax.inject.Inject

abstract class BaseArchActivity : BaseActivity() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as MewApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        inject(appComponent)
    }

    abstract fun inject(appComponent: ApplicationComponent)
}