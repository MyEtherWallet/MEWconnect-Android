package com.myetherwallet.mewconnect.core.ui.fragment

import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class BaseViewModelFragment : BaseDiFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
}