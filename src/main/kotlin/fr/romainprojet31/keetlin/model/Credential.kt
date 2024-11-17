package fr.romainprojet31.keetlin.model

open class Credential(id: Long?, var url: String, var username: String, pwd: String, val safeId: Long) : BModel(id) {
    var password: PasswordField

    init {
        password = PasswordField(pwd, false)
    }
}