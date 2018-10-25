package com.myetherwallet.mewconnect.feature.main.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.dialog.BaseDialogFragment


/**
 * Created by BArtWell on 11.09.2018.
 */

private const val TAG = "BackupWarningDialog"

class BackupWarningDialog : BaseDialogFragment() {

    companion object {

        fun newInstance() = BackupWarningDialog()
    }

    var listener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_backup_warning)
                .setPositiveButton(R.string.back_up) { _, _ ->
                    listener?.invoke()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() }
                .create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        }
        return alertDialog
    }

    override fun getFragmentTag() = TAG
}