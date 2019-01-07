package com.myetherwallet.mewconnect.feature.main.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.dialog.BaseDialogFragment
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import kotlinx.android.synthetic.main.dialog_rate.view.*
import javax.inject.Inject

/**
 * Created by BArtWell on 04.01.2019.
 */

private const val TAG = "RateDialog"

private const val EMAIL = "support@myetherwallet.com"
private const val SUBJECT_FORMAT = "MEWconnect %s for Android feedback"

private const val RATE_STARTS_THRESHOLD = 10

class RateDialog : BaseDialogFragment() {

    @Inject
    lateinit var preferences: PreferencesManager

    companion object {
        fun newInstance(application: MewApplication): RateDialog {
            val dialog = RateDialog()
            application.appComponent.inject(dialog)
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton(R.string.rate_button_rate) { _, _ ->
                    LaunchUtils.openMarket(context)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.rate_button_feedback) { dialog, _ ->
                    LaunchUtils.openMailApp(context, EMAIL, String.format(SUBJECT_FORMAT, BuildConfig.VERSION_NAME))
                    dialog.dismiss()
                }
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))
        }
        return alertDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_rate, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as AlertDialog).setView(view)
        view.rate_close.setOnClickListener {
            preferences.applicationPreferences.disableRateDialog()
            dialog.dismiss()
        }
    }

    override fun getFragmentTag() = TAG

    override fun show(fragmentManager: FragmentManager) {
        if (preferences.getCurrentWalletPreferences().isWalletExists() && preferences.applicationPreferences.isRateDialogEnabled()) {
            val startsCount = preferences.applicationPreferences.getRateStartsCount()
            if (startsCount < RATE_STARTS_THRESHOLD) {
                preferences.applicationPreferences.setRateStartsCount(startsCount + 1)
            } else {
                preferences.applicationPreferences.setRateStartsCount(0)
                super.show(fragmentManager)
            }
        }
    }
}