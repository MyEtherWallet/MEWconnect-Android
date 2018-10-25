package com.myetherwallet.mewconnect.feature.main.activity

import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by BArtWell on 20.09.2018.
 */

private const val DURATION = 1900L

class SplashActivity : BaseActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        val animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo)
        handler.postDelayed({
            finish()
            startActivity(MainActivity.createIntent(this))
            overridePendingTransition(0, 0)
        }, DURATION)
        splash_logo.startAnimation(animation)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }
}
