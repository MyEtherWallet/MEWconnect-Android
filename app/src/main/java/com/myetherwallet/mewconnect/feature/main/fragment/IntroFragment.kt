package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.auth.fragment.EnterRecoveryPhraseFragment
import com.myetherwallet.mewconnect.feature.main.adapter.IntroPagerAdapter
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlin.math.max

class IntroFragment : BaseFragment() {

    companion object {

        fun newInstance() = IntroFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayWidth = ApplicationUtils.getDisplaySize(requireContext()).width

        intro_tabs.setupWithViewPager(intro_pager, true)
        val adapter = IntroPagerAdapter()
        intro_pager.adapter = adapter

        val animatedPagesCount = adapter.count - 1
        intro_animation.setMaxProgress(animatedPagesCount * displayWidth)
        intro_animation.setPagesCount(animatedPagesCount)
        intro_animation.init()

        intro_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                intro_animation.x = max(displayWidth - (positionOffsetPixels + displayWidth * position), 0).toFloat()
                if (position > 0) {
                    intro_animation.setProgress(positionOffsetPixels + displayWidth * (position - 1), position - 1)
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })

        intro_setup.setOnClickListener { addFragment(PickPasswordFragment.newInstance()) }
        intro_restore_wallet.setOnClickListener { addFragment(EnterRecoveryPhraseFragment.newInstance()) }
    }

    override fun layoutId() = R.layout.fragment_intro
}