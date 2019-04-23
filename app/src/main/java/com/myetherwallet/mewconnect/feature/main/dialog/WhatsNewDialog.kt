package com.myetherwallet.mewconnect.feature.main.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.dialog.BaseDialogFragment

/**
 * Created by BArtWell on 22.03.2019.
 */

private const val TAG = "WhatsNewDialog"

class WhatsNewDialog : BaseDialogFragment() {

    companion object {

        fun newInstance() = WhatsNewDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.whats_new_title)
                .setMessage(R.string.whats_new)
                .setPositiveButton(R.string.close, null)
                .create()
    }

    override fun getFragmentTag() = TAG
}