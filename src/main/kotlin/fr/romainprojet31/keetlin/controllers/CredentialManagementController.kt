package fr.romainprojet31.keetlin.controllers

import fr.romainprojet31.keetlin.constants.NO_CHOSEN_SAFE_EXCEPTION
import fr.romainprojet31.keetlin.dao.CredentialRepositoryImpl
import fr.romainprojet31.keetlin.dao.SQLConnector
import fr.romainprojet31.keetlin.model.Credential
import fr.romainprojet31.keetlin.model.Safe
import fr.romainprojet31.keetlin.sec.decrypt
import fr.romainprojet31.keetlin.sec.encrypt
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.stage.Stage


class CredentialManagementController {
    /**
     * Not null if credential is on creation mode (credential == null)
     */
    var chosenSafe: Safe? = null
    var credential: Credential? = null
    private val formValid = SimpleBooleanProperty(false)

    @FXML
    lateinit var url: TextField

    @FXML
    lateinit var pwd: PasswordField

    @FXML
    lateinit var username: TextField

    @FXML
    lateinit var saveBtn: Button

    @FXML
    lateinit var scorePwd: Label

    @FXML
    fun checkRules(event: KeyEvent?) {
        val password = pwd.text
        val lengthOk = password.length >= 8
        var upperChar = false;
        var lowerChar = false;
        var numberChar = false
        for (chr: Char in password.toCharArray()) {
            if (!numberChar && chr.isDigit()) numberChar = true
            if (!upperChar && chr.isUpperCase()) upperChar = true
            if (!lowerChar && chr.isLowerCase()) lowerChar = true
        }
        val specialChar = password.matches(Regex("([,;:!?./§*%\\\\`@^])"))

        var score = 0
        if (lengthOk) score++
        if (upperChar) score++
        if (lowerChar) score++
        if (numberChar) score++
        if (specialChar) score++

        scorePwd.text = if (score < 2) "NOT GUD"
        else if (score < 4) "MEH"
        else if (score < 5) "NOICE"
        else "SO MUCH GUD !"

        handleForm(event)
    }

    @FXML
    fun handleForm(event: KeyEvent?) {
        formValid.set(pwd.text.isNotEmpty() && url.text.trim().isNotEmpty() && username.text.trim().isNotEmpty())
    }

    @FXML
    fun saveCredential(event: ActionEvent?) {
        if (!formValid.get()) {
            return
        }
        if (credential == null) {
            credential = Credential(
                id = null, url = url.text, pwd = encrypt(pwd.text), username = username.text, safeId = chosenSafe!!.id!!
            )
        } else {
            credential!!.url = url.text
            credential!!.password.encryptedPassword = encrypt(pwd.text)
            credential!!.username = username.text
        }
        (SQLConnector.instance.getRepo(CredentialRepositoryImpl::class.java) as CredentialRepositoryImpl)
            .save(credential!!)
        (pwd.scene.window as Stage).close()
    }

    fun init() {
        if (credential != null) {
            pwd.text = decrypt(credential!!.password.encryptedPassword)
            username.text = credential!!.username
            url.text = credential!!.url
        } else if (chosenSafe == null) {
            throw NO_CHOSEN_SAFE_EXCEPTION
        }
        saveBtn.disableProperty().bind(formValid.not())
    }
}
