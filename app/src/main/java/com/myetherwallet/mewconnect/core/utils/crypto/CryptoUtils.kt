package com.myetherwallet.mewconnect.core.utils.crypto

import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.macs.HMac
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.jcajce.provider.digest.Keccak
import org.spongycastle.jcajce.provider.digest.SHA3
import org.spongycastle.util.encoders.Hex
import java.security.MessageDigest

/**
 * Created by BArtWell on 17.07.2018.
 */

object CryptoUtils {

    fun sha3String(input: ByteArray): String = Hex.toHexString(sha3Bytes(input))

    fun sha3Bytes(input: ByteArray): ByteArray {
        val digest = SHA3.Digest256()
        digest.update(input)
        return digest.digest()
    }

    fun keccak256String(input: ByteArray) = Hex.toHexString(keccak256Bytes(input))

    fun keccak256Bytes(input: ByteArray): ByteArray {
        val digest = Keccak.Digest256()
        digest.update(input)
        return digest.digest()
    }

    fun sha512(data: ByteArray): ByteArray = MessageDigest.getInstance("SHA-512").digest(data)

    fun hMac(data: ByteArray, key: ByteArray): ByteArray {
        val digest = SHA256Digest()
        val hMac = HMac(digest)
        hMac.init(KeyParameter(key))
        hMac.reset()
        hMac.update(data, 0, data.size)
        val out = ByteArray(32)
        hMac.doFinal(out, 0)
        return out
    }
}
