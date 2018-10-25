package com.myetherwallet.mewconnect.core.ui.fragment

import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class BaseViewModelFragment : BaseDiFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
}