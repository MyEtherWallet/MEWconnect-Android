package com.myetherwallet.mewconnect.core.ui.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by BArtWell on 15.07.2018.
 */

abstract class BaseDialogFragment : DialogFragment() {

    open fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, getFragmentTag())
    }

    internal abstract fun getFragmentTag(): String
}