package com.myetherwallet.mewconnect.feature.register.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.myetherwallet.mewconnect.feature.register.interactor.CreateWallets
import javax.inject.Inject

/**
 * Created by BArtWell on 12.10.2018.
 */
class GeneratingViewModel
@Inject constructor(application: Application, private val generatingWallets: CreateWallets) : AndroidViewModel(application) {

    fun createWallets(password: String, mnemonic: String?, displayWidth: Int) {
        generatingWallets.execute(CreateWallets.Params(password, mnemonic, displayWidth)) {}
    }
}