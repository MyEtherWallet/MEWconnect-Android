package com.myetherwallet.mewconnect.feature.auth.utils

import android.os.Handler
import com.myetherwallet.mewconnect.core.persist.prefenreces.ApplicationPreferences
import java.util.concurrent.TimeUnit

/**
 * Created by BArtWell on 07.02.2019.
 */

private val TIMER_TIMEOUT = TimeUnit.MINUTES.toMillis(5)
private val ATTEMPTS_TIMEOUT = TimeUnit.MINUTES.toMillis(2)
private const val ATTEMPTS_LIMIT = 5

class AuthAttemptsHelper(
        private val handler: Handler,
        private val preferences: ApplicationPreferences,
        private val callback: (minute: Int, second: Int) -> Unit
) {

    private var isResumed = false

    fun check(): Boolean {
        if (preferences.getAuthFirstAttemptTime() + ATTEMPTS_TIMEOUT > getCurrentTime()) {
            if (preferences.getAuthAttemptsCount() + 1 >= ATTEMPTS_LIMIT) {
                reset()
                preferences.setAuthTimerTime(getCurrentTime())
                startTimer()
                return true
            }
        } else {
            preferences.resetAuthAttemptsCount()
            preferences.setAuthFirstAttemptTime(getCurrentTime())
        }
        preferences.incrementAuthAttemptsCount()
        return false
    }

    fun reset() {
        preferences.resetAuthAttemptsCount()
        preferences.setAuthFirstAttemptTime(0L)
    }

    fun resume() {
        if (!isResumed) {
            isResumed = true
            startTimer()
        }
    }

    fun pause() {
        isResumed = false
    }

    private fun startTimer() {
        if (isResumed) {
            if (checkTimer()) {
                callTimerCallback()
                handler.postDelayed({
                    startTimer()
                }, 1000L)
            } else {
                callback(0, 0)
            }
        }
    }

    private fun callTimerCallback() {
        val left = ((TIMER_TIMEOUT - (getCurrentTime() - preferences.getAuthTimerTime())) / 1000).toInt()
        val minute = left / 60
        callback(minute, left - 60 * minute)
    }

    private fun checkTimer(): Boolean {
        val time = preferences.getAuthTimerTime()
        if (time > 0) {
            return time + TIMER_TIMEOUT > getCurrentTime()
        }
        return false
    }

    private fun getCurrentTime() = System.currentTimeMillis()
}