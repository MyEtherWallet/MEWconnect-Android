package com.myetherwallet.mewconnect.core.extenstion

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.myetherwallet.mewconnect.core.platform.Failure

/**
 * Created by BArtWell on 02.09.2018.
 */

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer(body))

fun <L : LiveData<Failure>> LifecycleOwner.failure(liveData: L, body: (Failure?) -> Unit) =
        liveData.observe(this, Observer(body))
