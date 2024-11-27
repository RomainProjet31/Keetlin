package fr.romainprojet31.keetlin.controllers

import fr.romainprojet31.keetlin.model.Safe
import fr.romainprojet31.keetlin.sec.decrypt
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import java.net.URL
import java.util.*

class SafeManagementController : Initializable {
    @FXML
    private val safeName: TextField? = null

    @FXML
    private val safePwd: PasswordField? = null

    @FXML
    private val saveBtn: Button? = null

    var safe: Safe? = null

    private val formValid = SimpleBooleanProperty(false)

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        if (safe != null) {
            safeName!!.text = safe!!.name
            safePwd!!.text = decrypt(safe!!.masterPassword)
            updateFormValidity()
        }
    }

    @FXML
    fun handleForm(event: KeyEvent?) {
        updateFormValidity()
    }

    @FXML
    fun onSaveSafe(event: ActionEvent?) {
        if (safe == null) {
            safe = Safe(safeName!!.text, safePwd!!.text)
        } else {
            safe!!.name = safeName!!.text
            safe!!.masterPassword = safePwd!!.text
        }
    }

    private fun updateFormValidity() {
        formValid.set(safeName!!.text.isNotBlank() && safePwd!!.text.isNotBlank())
    }
}
