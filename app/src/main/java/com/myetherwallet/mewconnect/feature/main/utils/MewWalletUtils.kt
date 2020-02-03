package com.myetherwallet.mewconnect.feature.main.utils

import android.content.Context
import android.content.pm.PackageManager
import com.myetherwallet.mewconnect.core.utils.LaunchUtils


/**
 * Created by BArtWell on 03.02.2020.
 */

object MewWalletUtils {

    private const val MEW_WALLET_PACKAGE_NAME = "com.myetherwallet.mewwallet"

    fun isInstalled(context: Context) = try {
        context.packageManager.getPackageInfo(MEW_WALLET_PACKAGE_NAME, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    fun launchMarket(context: Context) = LaunchUtils.openMarket(context, MEW_WALLET_PACKAGE_NAME)

    fun launchApp(context: Context) = LaunchUtils.openApp(context, MEW_WALLET_PACKAGE_NAME)
}
