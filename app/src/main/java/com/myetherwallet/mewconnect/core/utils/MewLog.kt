package com.myetherwallet.mewconnect.core.utils

import android.util.Log
import com.myetherwallet.mewconnect.BuildConfig

/**
 * Created by BArtWell on 04.10.2018.
 */

object MewLog {

    private fun shouldDisplayLogs() = BuildConfig.DEBUG

    fun v(tag: String, msg: String) {
        if (shouldDisplayLogs()) {
            Log.v(tag, msg)
        }
    }


    fun v(tag: String, msg: String, tr: Throwable) {
        if (shouldDisplayLogs()) {
            Log.v(tag, msg, tr)
        }
    }

    fun d(tag: String, msg: String) {
        if (shouldDisplayLogs()) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable) {
        if (shouldDisplayLogs()) {
            Log.d(tag, msg, tr)
        }
    }

    fun i(tag: String, msg: String) {
        if (shouldDisplayLogs()) {
            Log.i(tag, msg)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable) {
        if (shouldDisplayLogs()) {
            Log.i(tag, msg, tr)
        }
    }

    fun w(tag: String, msg: String) {
        if (shouldDisplayLogs()) {
            Log.w(tag, msg)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable) {
        if (shouldDisplayLogs()) {
            Log.w(tag, msg, tr)
        }
    }

    fun e(tag: String, msg: String) {
        if (shouldDisplayLogs()) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        if (shouldDisplayLogs()) {
            Log.e(tag, msg, tr)
        }
    }
}