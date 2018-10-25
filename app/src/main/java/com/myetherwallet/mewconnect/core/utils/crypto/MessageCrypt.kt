package com.myetherwallet.mewconnect.core.utils.crypto

import com.myetherwallet.mewconnect.content.data.BaseMessage
import com.myetherwallet.mewconnect.content.data.EncryptedMessage
import com.myetherwallet.mewconnect.core.utils.HexUtils
import org.spongycastle.crypto.engines.AESEngine
import org.spongycastle.crypto.modes.CBCBlockCipher
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.crypto.params.ParametersWithIV
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import org.spongycastle.math.ec.ECPoint
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Sign
import java.math.BigInteger
import java.security.KeyFactory
import java.security.SecureRandom
import java.util.*
import javax.crypto.KeyAgreement


/**
 * Created by BArtWell on 24.07.2018.
 */

private const val KEY_PREFIX_FORMAT = "\u0019Ethereum Signed Message:\n%d"

class MessageCrypt(private val privateKey: String) {

    companion object {

        fun formatKeyWithPrefix(data: String): ByteArray {
            val keyData = data.toByteArray()
            val keyWithPrefix = String.format(KEY_PREFIX_FORMAT, keyData.size) + data
            return CryptoUtils.keccak256Bytes(keyWithPrefix.toByteArray())
        }
    }

    fun signMessage(data: String): String {
        val privateKey = BigInteger(this.privateKey, 16)
        val pubKey = Sign.publicKeyFromPrivate(privateKey)
        val keyPair = ECKeyPair(privateKey, pubKey)
        val signature = Sign.signMessage(formatKeyWithPrefix(data), keyPair, false)
        return signature.v.toString(16) + HexUtils.bytesToStringLowercase(signature.r) + HexUtils.bytesToStringLowercase(signature.s)
    }

    fun encrypt(data: BaseMessage) = encrypt(data.toByteArray())

    fun encrypt(data: ByteArray): EncryptedMessage {
        val connectPublicKey = publicKeyFromPrivateWithControl(privateKey)
        val initVector = SecureRandom().generateSeed(16)
//        val initVector = Hex.stringToBytes("4aff73f168d77ad250224efca77ca9d9")

        val ephemPrivateKey = SecureRandom().generateSeed(32)
//        val ephemPrivateKey = Hex.stringToBytes("0619c05ee445aa641d7640d6fc9db90924e3458fec68cb51cfa5d9ad9a059ab2")
        val ephemPublicKey = publicKeyFromPrivateWithControl(HexUtils.bytesToStringLowercase(ephemPrivateKey))

        val multipliedKeys = multiplyKeys(HexUtils.bytesToStringLowercase(ephemPrivateKey), connectPublicKey)
        val hashed = CryptoUtils.sha512(multipliedKeys)

        val encKey = Arrays.copyOfRange(hashed, 0, 32)
        val macKey = Arrays.copyOfRange(hashed, 32, hashed.size)

        val cipher = encryptAes256Cbc(initVector, encKey, data)

        val dataToHMac = initVector + ephemPublicKey + cipher
        val macData = CryptoUtils.hMac(dataToHMac, macKey)

        return EncryptedMessage(cipher, ephemPublicKey, initVector, macData)
    }

    fun decrypt(message: EncryptedMessage): ByteArray? {

        val multipliedKeys = multiplyKeys(privateKey, message.ephemPublicKey)
        val hashed = CryptoUtils.sha512(multipliedKeys)

        val encKey = Arrays.copyOfRange(hashed, 0, 32)
        val macKey = Arrays.copyOfRange(hashed, 32, hashed.size)

        val dataToHMac = message.iv + message.ephemPublicKey + message.ciphertext
        val macData = CryptoUtils.hMac(dataToHMac, macKey)

        if (!Arrays.equals(macData, message.mac)) {
            return null
        }

        return decryptAes256Cbc(encKey, message.ciphertext, message.iv)
    }

    private fun publicKeyFromPrivateWithControl(privateKeySource: String): ByteArray {
        val privateKey = BigInteger(privateKeySource, 16)
        val method = Sign::class.java.getDeclaredMethod("publicPointFromPrivate", BigInteger::class.java)
        method.isAccessible = true
        val point = method.invoke(null, privateKey) as ECPoint
        return point.getEncoded(false)
    }

    private fun multiplyKeys(privateKey: String, publicKey: ByteArray): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH", "SC")
        val keyFactory = KeyFactory.getInstance("ECDH", "SC")
        val params = ECNamedCurveTable.getParameterSpec("secp256k1")
        val privateKeyItem = keyFactory.generatePrivate(ECPrivateKeySpec(BigInteger(privateKey, 16), params))
        keyAgreement.init(privateKeyItem)
        val publicKeyItem = keyFactory.generatePublic(ECPublicKeySpec(params.curve.decodePoint(publicKey), params))
        keyAgreement.doPhase(publicKeyItem, true)
        return keyAgreement.generateSecret()
    }

    private fun encryptAes256Cbc(initVector: ByteArray, key: ByteArray, data: ByteArray): ByteArray {
        try {
            val cipher = PaddedBufferedBlockCipher(CBCBlockCipher(AESEngine()))

            cipher.init(true, ParametersWithIV(KeyParameter(key), initVector))
            val out = ByteArray(cipher.getOutputSize(data.size))

            val processed = cipher.processBytes(data, 0, data.size, out, 0)
            cipher.doFinal(out, processed)

            return out
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ByteArray(0)
    }

    private fun decryptAes256Cbc(encKey: ByteArray, cipher: ByteArray, initVector: ByteArray): ByteArray? {
        try {
            val blockCipher = PaddedBufferedBlockCipher(CBCBlockCipher(AESEngine()))
            blockCipher.init(false, ParametersWithIV(KeyParameter(encKey), initVector))
            val buffer = ByteArray(blockCipher.getOutputSize(cipher.size))
            var len = blockCipher.processBytes(cipher, 0, cipher.size, buffer, 0)
            len += blockCipher.doFinal(buffer, len)
            return Arrays.copyOfRange(buffer, 0, len)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}