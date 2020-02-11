package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.core.utils.DisplaySizeHelper
import com.myetherwallet.mewconnect.feature.auth.fragment.RestoreExistingWalletFragment
import com.myetherwallet.mewconnect.feature.main.adapter.IntroPagerAdapter
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlin.math.max

class IntroFragment : BaseFragment() {

    companion object {

        fun newInstance() = IntroFragment()
    }

    private val handler = Handler()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intro_tabs.setupWithViewPager(intro_pager, true)
        val adapter = IntroPagerAdapter()
        intro_pager.adapter = adapter

        val animatedPagesCount = adapter.count - 1
        intro_animation.setMaxProgress(animatedPagesCount * DisplaySizeHelper.width)
        intro_animation.setPagesCount(animatedPagesCount)
        intro_animation.init()

        intro_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                addOnResumeListener {
                    intro_animation.x = max(DisplaySizeHelper.width - (positionOffsetPixels + DisplaySizeHelper.width * position), 0).toFloat()
                    if (position > 0) {
                        intro_animation.setProgress(positionOffsetPixels + DisplaySizeHelper.width * (position - 1), position - 1)
                    }
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })

        intro_setup.setOnClickListener { addFragment(PickPasswordFragment.newInstance()) }
        intro_restore_wallet.setOnClickListener { addFragment(RestoreExistingWalletFragment.newInstance()) }
    }

    override fun layoutId() = R.layout.fragment_intro
}