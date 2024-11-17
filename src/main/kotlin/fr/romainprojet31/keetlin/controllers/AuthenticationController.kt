package fr.romainprojet31.keetlin.controllers

import fr.romainprojet31.keetlin.dao.SQLConnector
import fr.romainprojet31.keetlin.dao.SafeRepositoryImpl
import fr.romainprojet31.keetlin.model.Safe
import fr.romainprojet31.keetlin.view.SceneManager
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.StringConverter
import java.net.URL
import java.util.*

@Suppress("UNCHECKED_CAST")
class AuthenticationController : Initializable {
    private val errorMsg = "Authentication failed"
    private val successMsg = "Authentication succeeded"

    @FXML
    lateinit var safeLstCbx: ComboBox<Safe>

    @FXML
    lateinit var safePwd: PasswordField

    @FXML
    lateinit var confirmationMsg: Label

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        confirmationMsg.visibleProperty().set(false)
        val lstSafe = SQLConnector.instance.getRepo(SafeRepositoryImpl::class.java).all() as ArrayList<Safe>
        safeLstCbx.items.addAll(lstSafe)
        safeLstCbx.converter = object : StringConverter<Safe>() {
            override fun toString(safe: Safe?): String {
                return safe?.name ?: ""
            }

            override fun fromString(string: String?): Safe? {
                return safeLstCbx.items.find { it.name == string }
            }
        }

        if (!safeLstCbx.items.isEmpty()) {
            safeLstCbx.selectionModel.select(0)
        }
    }

    @FXML
    fun submitAuthenticationByKeyPressed(event: KeyEvent?) {
        if (KeyCode.ENTER?.equals(event?.code)) this.submitAuthentication()
    }


    @FXML
    fun submitAuthentication() {
        val isOk = safeLstCbx.selectionModel.selectedItem.checkPwd(safePwd.text)
        confirmationMsg.visibleProperty().set(true)
        if (isOk) {
            confirmationMsg.text = successMsg
            confirmationMsg.styleClass.add("bg-success")
            confirmationMsg.styleClass.remove("bg-danger")
            val fxmlLoader = SceneManager.instance.getFxmlLoader("keetlin-view.fxml")
            SceneManager.instance.load(fxmlLoader.load<Parent>())
            val keetlinViewCtrl = fxmlLoader.getController<KeetlinViewController>()
            keetlinViewCtrl.chosenSafe = safeLstCbx.selectionModel.selectedItem
            keetlinViewCtrl.init()
        } else {
            confirmationMsg.text = errorMsg
            confirmationMsg.styleClass.add("bg-danger")
            confirmationMsg.styleClass.remove("bg-success")
        }
    }
}