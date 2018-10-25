package com.myetherwallet.mewconnect.core.ui.dialog

import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager

/**
 * Created by BArtWell on 15.07.2018.
 */

abstract class BaseDialogFragment : DialogFragment() {

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, getFragmentTag())
    }

    internal abstract fun getFragmentTag(): String
}