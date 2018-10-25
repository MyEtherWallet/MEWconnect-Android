package com.myetherwallet.mewconnect.feature.main.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.activity.BaseDiActivity
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.main.fragment.IntroFragment
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import javax.inject.Inject

/**
 * Created by BArtWell on 30.06.2018.
 */

class MainActivity : BaseDiActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (preferences.getCurrentWalletPreferences().isWalletExists()) {
            replaceFragment(AuthFragment.newInstance())
        } else {
            replaceFragment(IntroFragment.newInstance())
        }

        setStatusBarColor()
    }

    override fun onResume() {
        super.onResume()
        SocketService.start(this)
    }

    override fun onPause() {
        super.onPause()
        SocketService.shutdownDelayed(this)
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.toString())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        fragmentTransaction.commit()
    }

    fun addFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.toString())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun addOrReplaceFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val previous = fragmentManager.findFragmentByTag(tag)
        if (previous == null) {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        } else {
            fragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        if (fragment !is BaseFragment || !fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }
}

