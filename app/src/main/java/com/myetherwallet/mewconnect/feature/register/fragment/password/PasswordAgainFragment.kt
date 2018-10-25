package com.myetherwallet.mewconnect.feature.register.fragment.password

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.extenstion.getString
import com.myetherwallet.mewconnect.core.utils.StringUtils
import com.myetherwallet.mewconnect.feature.register.fragment.GeneratingFragment
import kotlinx.android.synthetic.main.fragment_pick_password.view.*

private const val EXTRA_PASSWORD = "password"
private const val EXTRA_MNEMONIC = "mnemonic"

class PasswordAgainFragment : BasePickPasswordFragment(), Toolbar.OnMenuItemClickListener {

    companion object {

        fun newInstance(password: String, mnemonic: String?): PasswordAgainFragment {
            val fragment = PasswordAgainFragment()
            val arguments = Bundle()
            arguments.putString(EXTRA_PASSWORD, password)
            mnemonic?.let {
                arguments.putString(EXTRA_MNEMONIC, mnemonic)
            }
            fragment.arguments = arguments
            return fragment
        }
    }

    private var previousPassword: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.pick_password_description.text = StringUtils.fromHtml(getString(getDescription()))
        previousPassword = arguments?.getString(EXTRA_PASSWORD)
    }

    override fun getTitle() = R.string.password_again_title

    override fun getDescription() = R.string.password_again_description

    override fun canGoNext(password: String) = password == previousPassword

    override fun onNextClick(isNextEnabled: Boolean, password: String) {
        if (isNextEnabled) {
            replaceFragment(GeneratingFragment.newInstance(password, getString(EXTRA_MNEMONIC)))
        } else {
            showPasswordError(getString(R.string.password_again_error))
        }
    }
}