package com.myetherwallet.mewconnect.core.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.myetherwallet.mewconnect.core.utils.KeyboardUtils
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity

abstract class BaseFragment : Fragment() {

    private val onResumeListeners = mutableListOf<() -> Unit>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(layoutId(), container, false)

    override fun onResume() {
        super.onResume()
        for (listener in onResumeListeners) {
            listener()
        }
        onResumeListeners.clear()
    }

    abstract fun layoutId(): Int

    protected fun replaceFragment(fragment: Fragment) {
        KeyboardUtils.hideKeyboard(activity)
        getMainActivity()?.replaceFragment(fragment)
    }

    protected fun addFragment(fragment: Fragment) {
        KeyboardUtils.hideKeyboard(activity)
        getMainActivity()?.addFragment(fragment)
    }

    protected fun addOrReplaceFragment(fragment: Fragment, tag: String) {
        KeyboardUtils.hideKeyboard(activity)
        getMainActivity()?.addOrReplaceFragment(fragment, tag)
    }

    protected fun close() {
        KeyboardUtils.hideKeyboard(activity)
        getMainActivity()?.closeFragment()
    }

    protected fun closeToFirst() {
        KeyboardUtils.hideKeyboard(activity)
        getMainActivity()?.closeFragmentsToFirst()
    }

    private fun getMainActivity(): MainActivity? {
        val activityToCheck = activity
        return if (activityToCheck is MainActivity) {
            activityToCheck
        } else {
            null
        }
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
        val window = activity?.window
        if (enabled) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun addOnResumeListener(listener: () -> Unit) {
        if (isResumed) {
            listener.invoke()
        } else {
            onResumeListeners.add(listener)
        }
    }

    open fun onBackPressed() = false
}