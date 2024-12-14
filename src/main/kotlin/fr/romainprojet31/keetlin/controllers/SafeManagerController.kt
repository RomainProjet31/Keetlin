package fr.romainprojet31.keetlin.controllers

import fr.romainprojet31.keetlin.dao.SQLConnector
import fr.romainprojet31.keetlin.dao.SafeRepositoryImpl
import fr.romainprojet31.keetlin.model.Safe
import fr.romainprojet31.keetlin.sec.decrypt
import fr.romainprojet31.keetlin.view.SceneManager
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
    lateinit var safeName: TextField

    @FXML
    lateinit var safePwd: PasswordField

    @FXML
    lateinit var saveBtn: Button

    var safe: Safe? = null

    private val formValid = SimpleBooleanProperty(false)

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        init()
    }

    fun init() {
        saveBtn.disableProperty().bind(formValid.not())
        if (safe != null) {
            safeName.text = safe!!.name
            updateFormValidity()
        }
    }

    @FXML
    fun handleForm(event: KeyEvent?) {
        updateFormValidity()
    }

    @FXML
    fun goToHome() {
        SceneManager.instance.load("authentication.fxml")
    }

    @FXML
    fun onSaveSafe(event: ActionEvent?) {
        if (safe == null) {
            safe = Safe(safeName.text, safePwd.text)
        } else {
            safe!!.name = safeName.text
            safe!!.masterPassword = safePwd.text
        }

        safe!!.id =
            (SQLConnector.instance.getRepo(SafeRepositoryImpl::class.java) as SafeRepositoryImpl).save(safe!!).id
        SceneManager.instance.load("authentication.fxml")
    }

    private fun updateFormValidity() {
        val safePwdOk = safePwd.text.isNotBlank() && (safe == null || !safe!!.checkPwd(safePwd.text))
        formValid.set(safeName.text.isNotBlank() && safePwdOk)
    }
}
