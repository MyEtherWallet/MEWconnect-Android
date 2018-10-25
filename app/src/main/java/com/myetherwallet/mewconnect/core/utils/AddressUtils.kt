package com.myetherwallet.mewconnect.core.utils

import pm.gnosis.model.Solidity

/**
 * Created by BArtWell on 28.08.2018.
 */

object AddressUtils {

    fun toSolidityAddress(address: String) = Solidity.Address(HexUtils.toBigInteger(address))
}