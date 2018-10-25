package com.myetherwallet.mewconnect.core.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.myetherwallet.mewconnect.core.utils.KeyboardUtils
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(layoutId(), container, false)

    abstract fun layoutId(): Int

    protected fun replaceFragment(fragment: Fragment) {
        KeyboardUtils.hideKeyboard(activity)
        (activity as MainActivity).replaceFragment(fragment)
    }

    protected fun addFragment(fragment: Fragment) {
        KeyboardUtils.hideKeyboard(activity)
        (activity as MainActivity).addFragment(fragment)
    }

    protected fun addOrReplaceFragment(fragment: Fragment, tag: String) {
        KeyboardUtils.hideKeyboard(activity)
        (activity as MainActivity).addOrReplaceFragment(fragment, tag)
    }

    fun setLightStatusBar(enabled: Boolean) {
        val decorView = activity?.window?.decorView
        decorView?.let {
            val flags = decorView.systemUiVisibility
            decorView.systemUiVisibility = if (enabled) {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    fun setWindowSecure(enabled: Boolean) {
        if (enabled) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    protected fun close() {
        KeyboardUtils.hideKeyboard(activity)
        fragmentManager?.popBackStackImmediate()
    }

    open fun onBackPressed() = false
}