package com.myetherwallet.mewconnect.feature.main.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.fragment.app.Fragment
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.activity.BaseDiActivity
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.main.fragment.IntroFragment
import com.myetherwallet.mewconnect.feature.main.fragment.WhatsNewFragment
import com.myetherwallet.mewconnect.feature.main.utils.FragmentTransactor
import com.myetherwallet.mewconnect.feature.scan.receiver.ServiceAlarmReceiver
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by BArtWell on 30.06.2018.
 */

private val NO_LOCK_LIMIT = TimeUnit.MINUTES.toMillis(2)

class MainActivity : BaseDiActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private lateinit var fragmentTransactor: FragmentTransactor
    private val handler = Handler()
    private var activityPaused = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentTransactor = FragmentTransactor()

        if (preferences.getCurrentWalletPreferences().isWalletExists()) {
            lockApp()
        } else {
            replaceFragment(IntroFragment.newInstance())
        }

        setStatusBarColor()

        if (preferences.applicationPreferences.shouldShowWhatsNewDialog()) {
            handler.postDelayed({
                addFragment(WhatsNewFragment.newInstance())
            }, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        ServiceAlarmReceiver.cancel(this)
        activityPaused = 0
    }

    override fun onPause() {
        super.onPause()
        ServiceAlarmReceiver.schedule(this)
        activityPaused = SystemClock.elapsedRealtime()
    }

    override fun onPostResume() {
        super.onPostResume()
        fragmentTransactor.resume(supportFragmentManager)
    }

    override fun onRestart() {
        val fragment = getCurrentFragment()
        if (fragment != null && fragment !is AuthFragment && SystemClock.elapsedRealtime() - activityPaused > NO_LOCK_LIMIT) {
            lockApp()
        }
        super.onRestart()
    }

    private fun lockApp() {
        fragmentTransactor.replaceNow(supportFragmentManager, AuthFragment.newInstance())
    }

    fun addFragment(fragment: Fragment) {
        fragmentTransactor.add(supportFragmentManager, fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        fragmentTransactor.replace(supportFragmentManager, fragment)
    }

    fun addOrReplaceFragment(fragment: Fragment, tag: String) {
        fragmentTransactor.addOrReplace(supportFragmentManager, fragment, tag)
    }

    fun closeFragment() {
        fragmentTransactor.pop(supportFragmentManager)
    }

    fun closeFragmentsToFirst() {
        fragmentTransactor.popToFirst(supportFragmentManager)
    }

    private fun getCurrentFragment() = supportFragmentManager.findFragmentById(R.id.main_fragment_container)

    override fun onBackPressed() {
        val fragment = getCurrentFragment()
        if (fragment !is BaseFragment || !fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }
}

