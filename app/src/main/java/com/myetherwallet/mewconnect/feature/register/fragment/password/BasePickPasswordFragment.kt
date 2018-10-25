package com.myetherwallet.mewconnect.feature.register.fragment.password

import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.callback.EmptyTextWatcher
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.core.utils.KeyboardStateObserver
import com.myetherwallet.mewconnect.core.utils.KeyboardUtils
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.android.synthetic.main.fragment_pick_password.*
import kotlinx.android.synthetic.main.fragment_pick_password.view.*

private const val STRENGTH_WEAK = 0
private const val STRENGTH_FAIR = 1
private const val STRENGTH_GOOD = 2
private const val STRENGTH_STRONG = 3
private const val STRENGTH_VERY_STRONG = 4

abstract class BasePickPasswordFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    private lateinit var keyboardStateObserver: KeyboardStateObserver
    private var isNextEnabled: Boolean = false
        set(value) {
            field = value
            val menuItem = pick_password_toolbar.getMenu().findItem(R.id.next)
            if (value) {
                menuItem.setIcon(R.drawable.ic_action_next_enabled)
            } else {
                menuItem.setIcon(R.drawable.ic_action_next_disabled)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyboardStateObserver = KeyboardStateObserver(view.pick_password_content_container)
        keyboardStateObserver.listener = {
            if (it) {
                pick_password_scroll.fullScroll(View.FOCUS_DOWN)
            }
        }

        view.pick_password_toolbar.setNavigationIcon(R.drawable.ic_action_close)
        view.pick_password_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })
        view.pick_password_toolbar.inflateMenu(R.menu.forward)
        view.pick_password_toolbar.setOnMenuItemClickListener(this)

        view.pick_password_text.addTextChangedListener(object : EmptyTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onPasswordChanged(s.toString())
            }
        })

        view.pick_password_title.setText(getTitle())
        view.pick_password_description.setText(getDescription())
        isNextEnabled = false

        KeyboardUtils.showKeyboard(view.pick_password_text)
        view.pick_password_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onNextClick(isNextEnabled, view.pick_password_text.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun onPasswordChanged(password: String) {
        if (password.isEmpty()) {
            view?.pick_password_strength_container?.visibility = GONE
            isNextEnabled = false
        } else {
            val zxcvbn = Zxcvbn()
            val strength = zxcvbn.measure(password)
            when (strength.score) {
                STRENGTH_WEAK, STRENGTH_FAIR -> setStrength(R.color.pick_password_strength_weak, R.string.pick_password_strength_weak, 25)
                STRENGTH_GOOD -> setStrength(R.color.pick_password_strength_so_so, R.string.pick_password_strength_so_so, 50)
                STRENGTH_STRONG -> setStrength(R.color.pick_password_strength_good, R.string.pick_password_strength_good, 75)
                STRENGTH_VERY_STRONG -> setStrength(R.color.pick_password_strength_great, R.string.pick_password_strength_great, 100)
            }
            view?.pick_password_strength_container?.visibility = VISIBLE
            isNextEnabled = canGoNext(password)
        }
        if (view?.pick_password_input_layout?.isErrorEnabled == true) {
            view?.pick_password_input_layout?.isErrorEnabled = false
        }
    }

    open fun canGoNext(password: String) = true

    private fun setStrength(@ColorRes color: Int, @StringRes text: Int, progress: Int) {
        view?.pick_password_strength_progress?.progress = progress
        view?.pick_password_strength_progress?.progressTintList = ContextCompat.getColorStateList(requireContext(), color)
        view?.pick_password_strength_text?.setText(text)
    }

    fun showPasswordError(error: String) {
        view?.pick_password_input_layout?.error = error
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.next) {
            onNextClick(isNextEnabled, view?.pick_password_text?.text.toString())
            return true
        }
        return false
    }

    abstract fun onNextClick(isNextEnabled: Boolean, password: String)

    @StringRes
    abstract fun getTitle(): Int

    @StringRes
    abstract fun getDescription(): Int

    override fun layoutId() = R.layout.fragment_pick_password
}