package com.myetherwallet.mewconnect.core.ui.activity

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.myetherwallet.mewconnect.R

abstract class BaseActivity : AppCompatActivity() {

    fun setStatusBarColor(@ColorRes color: Int = R.color.status_bar_white) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}