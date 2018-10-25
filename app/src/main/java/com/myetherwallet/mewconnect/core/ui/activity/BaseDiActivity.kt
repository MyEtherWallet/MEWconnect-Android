package com.myetherwallet.mewconnect.core.ui.activity

import android.os.Bundle
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.di.ApplicationComponent

/**
 * Created by BArtWell on 10.07.2018.
 */
abstract class BaseDiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject((application as MewApplication).appComponent)
    }

    abstract fun inject(appComponent: ApplicationComponent)
}