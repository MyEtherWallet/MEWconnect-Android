package com.myetherwallet.mewconnect.core.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import java.util.*

object KeyboardUtils {

    fun hideKeyboard(activity: Activity?) {
        if (activity == null) return
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun hideKeyboard(view: View) {
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun showKeyboard(activity: Activity) {
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputManager.showSoftInput(activity.currentFocus, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun showKeyboard(view: View) {
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showKeyboard(view: View, delay: Long) {
        view.postDelayed({ showKeyboard(view) }, delay)
    }

    fun showSoftKeyboardFor(context: Context, view: View?) {
        try {
            val mgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                view.requestFocus()
                mgr.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            } else {
                mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    fun hideSoftKeyboardFor(activity: Activity, view: View?) {
        if (view != null) {
            try {
                val mgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        } else {
            try {
                val mgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                mgr.hideSoftInputFromWindow(activity.findViewById<View>(android.R.id.content).windowToken, 0)
                mgr.hideSoftInputFromWindow(activity.findViewById<View>(android.R.id.content).applicationWindowToken, 0)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
    }

    fun showSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun getLocaleForKeyboard(activity: Activity): String {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val imz = imm.currentInputMethodSubtype
        val localeString = imz.locale
        val locale = Locale(localeString)
        return locale.displayLanguage
    }
}