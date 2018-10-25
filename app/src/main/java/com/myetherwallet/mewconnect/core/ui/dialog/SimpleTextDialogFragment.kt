package com.myetherwallet.mewconnect.core.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog

/**
 * Created by BArtWell on 15.07.2018.
 */

private const val TAG = "SimpleTextDialogFragment"
private const val EXTRA_TITLE = "title"
private const val EXTRA_TEXT = "text"

class SimpleTextDialogFragment : BaseDialogFragment() {

    companion object {

        fun newInstance(text: String, title: String? = null): SimpleTextDialogFragment {
            val fragment = SimpleTextDialogFragment()
            val arguments = Bundle()
            if (title != null) {
                arguments.putString(EXTRA_TITLE, title)
            }
            arguments.putString(EXTRA_TEXT, text)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(arguments?.getString(EXTRA_TITLE))
        builder.setMessage(arguments?.getString(EXTRA_TEXT))
        builder.setPositiveButton(android.R.string.ok, null)
        return builder.create()
    }

    override fun getFragmentTag() = TAG
}