package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.MessageToSign
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.scan.viewmodel.SignMessageViewModel
import kotlinx.android.synthetic.main.fragment_sign_message.*
import javax.inject.Inject

/**
 * Created by BArtWell on 29.09.2018.
 */

private const val EXTRA_MESSAGE = "message"
private const val AUTH_REQUEST_CODE = 101

class SignMessageFragment : BaseViewModelFragment(), AuthCallback {

    companion object {

        const val TAG = "SignMessageFragment"

        fun newInstance(message: MessageToSign): SignMessageFragment {
            val fragment = SignMessageFragment()
            val arguments = Bundle()
            arguments.putParcelable(EXTRA_MESSAGE, message)
            fragment.arguments = arguments
            return fragment
        }
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private lateinit var viewModel: SignMessageViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = viewModel()

        sign_message_text.setText(arguments?.getParcelable<MessageToSign>(EXTRA_MESSAGE)?.text)

        sign_message_cancel.setOnClickListener { close() }

        sign_message_confirm.setOnClickListener { _ ->
            val authFragment = AuthFragment.newInstance()
            authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
            addFragment(authFragment)
        }
    }

    override fun onAuthResult(password: String) {
        arguments?.getParcelable<MessageToSign>(EXTRA_MESSAGE)?.let {
            viewModel.signMessage(it, preferences, password)
            addOrReplaceFragment(MessageSignedFragment.newInstance(), TAG)
        }
    }

    override fun onAuthCancel() {

    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_sign_message

}