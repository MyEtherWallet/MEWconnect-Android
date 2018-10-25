package com.myetherwallet.mewconnect.core.ui.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.di.ApplicationComponent

/**
 * Created by BArtWell on 30.08.2018.
 */
abstract class BaseDiFragment : BaseFragment() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as MewApplication).appComponent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inject(appComponent)
    }

    abstract fun inject(appComponent: ApplicationComponent)
}