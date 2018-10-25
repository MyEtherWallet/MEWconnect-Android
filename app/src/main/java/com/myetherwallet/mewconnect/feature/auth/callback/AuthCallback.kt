package com.myetherwallet.mewconnect.feature.auth.callback

/**
 * Created by BArtWell on 10.09.2018.
 */
interface AuthCallback {

    fun onAuthResult(password:String)

    fun onAuthCancel()
}