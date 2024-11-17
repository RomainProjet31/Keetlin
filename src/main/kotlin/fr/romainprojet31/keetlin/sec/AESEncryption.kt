package fr.romainprojet31.keetlin.sec

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


private const val encryptionKeyString: String = "thisisa128bitkey"


fun genKey(): SecretKey? {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(128)
    return keyGenerator.generateKey()
}

fun generateIv(): IvParameterSpec? {
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    return IvParameterSpec(iv)
}

private fun getCipherInit(mode: Int, ivParameterSpec: IvParameterSpec? = generateIv()): Cipher {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKey = SecretKeySpec(encryptionKeyString.toByteArray(), "AES")
    cipher.init(mode, secretKey, ivParameterSpec)
    return cipher
}

@Throws(
    NoSuchPaddingException::class,
    NoSuchAlgorithmException::class,
    InvalidAlgorithmParameterException::class,
    InvalidKeyException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
fun encrypt(pwd: String): String {
    val cipher = getCipherInit(Cipher.ENCRYPT_MODE)
    return Base64 // stocke l'IV avec le texte chiffré
        .getEncoder()
        .encodeToString(cipher.iv + cipher.doFinal(pwd.toByteArray()))
}

@Throws(
    NoSuchPaddingException::class,
    NoSuchAlgorithmException::class,
    InvalidAlgorithmParameterException::class,
    InvalidKeyException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
fun decrypt(cipherText: String?): String {
    val cipherData = Base64.getDecoder().decode(cipherText)
    // Sépare l'IV (premiers 16 octets) du texte chiffré
    val iv = IvParameterSpec(cipherData.copyOfRange(0, 16))
    val cipherTextBytes = cipherData.copyOfRange(16, cipherData.size)
    val cipher = getCipherInit(Cipher.DECRYPT_MODE, iv)
    val plainText = cipher.doFinal(cipherTextBytes)
    return String(plainText)
}