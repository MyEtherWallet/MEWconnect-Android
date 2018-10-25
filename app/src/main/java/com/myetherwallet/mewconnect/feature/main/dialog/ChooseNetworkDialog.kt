package com.myetherwallet.mewconnect.feature.main.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.ui.dialog.BaseDialogFragment


/**
 * Created by BArtWell on 04.09.2018.
 */

private const val TAG = "ChooseNetworkDialog"

class ChooseNetworkDialog : BaseDialogFragment() {

    companion object {

        fun newInstance() = ChooseNetworkDialog()
    }

    var listener: ((network: Network) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(requireContext())
                    .setTitle(R.string.wallet_network_title)
                    .setItems(Network.getTitles(requireContext())) { _, i -> listener?.invoke(Network.values()[i]) }
                    .create()

    override fun getFragmentTag() = TAG
}