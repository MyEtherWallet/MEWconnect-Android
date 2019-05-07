package com.myetherwallet.mewconnect.core.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog


/**
 * Created by BArtWell on 11.02.2018.
 */

private const val TAG = "ConfirmDialog"
private const val EXTRA_TITLE = "title"
private const val EXTRA_TEXT = "text"

class ConfirmDialog : BaseDialogFragment() {

    companion object {
        fun newInstance(text: String, title: String? = null): ConfirmDialog {
            val fragment = ConfirmDialog()
            val arguments = Bundle()
            if (title != null) {
                arguments.putString(EXTRA_TITLE, title)
            }
            arguments.putString(EXTRA_TEXT, text)
            fragment.arguments = arguments
            return fragment
        }
    }

    var listener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(arguments?.getString(EXTRA_TITLE))
        builder.setMessage(arguments?.getString(EXTRA_TEXT))
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            listener?.invoke()
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }

    override fun getFragmentTag() = TAG
}
