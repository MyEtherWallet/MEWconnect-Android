package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.auth.utils.BiometricUtils
import kotlinx.android.synthetic.main.fragment_fingerprint_available.*
import javax.inject.Inject

/**
 * Created by BArtWell on 29.05.2019.
 */

private const val AUTH_REQUEST_CODE = 103

class FingerprintAvailableFragment : BaseDiFragment(), AuthCallback {

    companion object {
        const val FINGERPRINT_FRAGMENTS_TAG = "fingerprint"

        fun newInstance() = FingerprintAvailableFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fingerprint_available_enable.setOnClickListener {
            val authFragment = AuthFragment.newInstance(false)
            authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
            addFragment(authFragment)
        }
        fingerprint_available_set_up_later.setOnClickListener { close() }
    }

    override fun onAuthResult(helper: BaseEncryptHelper, keyStore: KeyStore) {
        close()
        if (BiometricUtils.encryptData(requireContext(), helper, keyStore, preferences)) {
            addOnResumeListener {
                addOrReplaceFragment(FingerprintEnabledFragment.newInstance(), FINGERPRINT_FRAGMENTS_TAG)
            }
        }
    }

    override fun onAuthCancel() {

    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_fingerprint_available
}