package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.callback.EmptyTextWatcher
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.KeyboardStateObserver
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_enter_recovery_phrase.*
import org.web3j.crypto.MnemonicUtils


/**
 * Created by BArtWell on 13.08.2018.
 */

private const val WORDS_COUNT = 24

class EnterRecoveryPhraseFragment : BaseDiFragment() {

    companion object {

        fun newInstance() = EnterRecoveryPhraseFragment()
    }

    private lateinit var keyboardStateObserver: KeyboardStateObserver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enter_recovery_phrase_toolbar.setNavigationIcon(R.drawable.ic_action_close)
        enter_recovery_phrase_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })
        enter_recovery_phrase_toolbar.inflateMenu(R.menu.forward)
        enter_recovery_phrase_toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
            onNextClick()
            true
        })

        enter_recovery_phrase_text.addTextChangedListener(object : EmptyTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enter_recovery_phrase_layout.isErrorEnabled = false
            }
        })

        keyboardStateObserver = KeyboardStateObserver(enter_recovery_phrase_content_container)
        keyboardStateObserver.listener = {
            if (it) {
                enter_recovery_phrase_scroll.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun onNextClick() {
        var text = enter_recovery_phrase_text.text.toString()
        text = text.replace(Regex("\\s+"), " ")
        text = text.trim()
        val wordsCount = text.count { " ".contains(it) } + 1
        if (wordsCount == WORDS_COUNT && MnemonicUtils.validateMnemonic(text)) {
            addFragment(PickPasswordFragment.newInstance(text))
        } else {
            enter_recovery_phrase_layout.error = getString(R.string.enter_recovery_phrase_error)
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_enter_recovery_phrase
}