package fr.romainprojet31.keetlin.observable

import fr.romainprojet31.keetlin.sec.decrypt
import javafx.beans.property.SimpleStringProperty

class PasswordProperty(value: String) : SimpleStringProperty() {
    var displayPwd: Boolean = false
        set(value) {
            if (value) {
                set(decrypt(encryptedPassword))
            } else {
                set("******")
            }
            field = value
        }
    private val encryptedPassword = value

    init {
        displayPwd = false
    }

    public fun getPlainPassword(): String {
        return if (displayPwd) get() else decrypt(encryptedPassword)
    }
}