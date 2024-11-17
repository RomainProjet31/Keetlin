package fr.romainprojet31.keetlin.model

import fr.romainprojet31.keetlin.sec.encrypt


class PasswordField(pwd: String, plainText: Boolean) {
    var encryptedPassword: String

    init {
        encryptedPassword = if (plainText) encrypt(pwd) else pwd
    }

    fun checkPassword(plainText: String): Boolean {
        return encrypt(plainText) == encryptedPassword
    }
}