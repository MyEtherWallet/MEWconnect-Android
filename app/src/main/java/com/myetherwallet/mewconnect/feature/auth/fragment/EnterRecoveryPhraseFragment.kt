package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.callback.EmptyTextWatcher
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.KeyboardStateObserver
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_enter_recovery_phrase.*
import org.bitcoinj.crypto.MnemonicCode


/**
 * Created by BArtWell on 13.08.2018.
 */

private val WORDS_COUNTS = arrayOf(12, 15, 18, 21, 24)

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

        // Fix scroll of EditText inside ScrollView
        enter_recovery_phrase_text.setOnTouchListener { editText, event ->
            editText.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                editText.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
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
        val text = enter_recovery_phrase_text.text.toString()
                .replace(Regex("\\s+"), " ")
                .trim()
                .toLowerCase()
        val wordsCount = text.count { " ".contains(it) } + 1
        if (WORDS_COUNTS.contains(wordsCount) && validateMnemonic(text)) {
            addFragment(PickPasswordFragment.newInstance(text))
        } else {
            enter_recovery_phrase_layout.error = getString(R.string.enter_recovery_phrase_error)
        }
    }

    private fun validateMnemonic(mnemonic: String): Boolean {
        try {
            MnemonicCode.INSTANCE.check(ArrayList(mnemonic.split(" ")))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_enter_recovery_phrase
}