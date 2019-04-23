package com.myetherwallet.mewconnect.core.extenstion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment

/**
 * Created by BArtWell on 16.07.2018.
 */

inline fun <reified T : ViewModel> Fragment.viewModel(factory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = ViewModelProviders.of(this, factory)[T::class.java]
    vm.body()
    return vm
}

inline fun <reified T : ViewModel> Fragment.viewModel(factory: ViewModelProvider.Factory): T = ViewModelProviders.of(this, factory)[T::class.java]

inline fun <reified T : ViewModel> Fragment.viewModel(): T = ViewModelProviders.of(this)[T::class.java]

fun Fragment.getString(key: String): String? {
    val arguments = this.arguments
    if (arguments != null && arguments.containsKey(key)) {
        return arguments.getString(key)
    } else {
        return null
    }
}