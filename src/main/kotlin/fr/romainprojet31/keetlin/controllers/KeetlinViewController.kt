package fr.romainprojet31.keetlin.controllers

import fr.romainprojet31.keetlin.KeetlinApplication
import fr.romainprojet31.keetlin.constants.NO_CHOSEN_SAFE_EXCEPTION
import fr.romainprojet31.keetlin.dao.CredentialRepositoryImpl
import fr.romainprojet31.keetlin.dao.SQLConnector
import fr.romainprojet31.keetlin.dao.SafeRepositoryImpl
import fr.romainprojet31.keetlin.model.Credential
import fr.romainprojet31.keetlin.model.Safe
import fr.romainprojet31.keetlin.observable.DisplayCredential
import fr.romainprojet31.keetlin.view.Icon
import fr.romainprojet31.keetlin.view.SceneManager
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.controlsfx.control.Notifications


@Suppress("UNCHECKED_CAST")
class KeetlinViewController {
    private val maxSecCopy: Int = 10
    private var curSecCopy: Int = 0

    @FXML
    private lateinit var tblPwd: TableView<DisplayCredential>

    @FXML
    private lateinit var progressCopy: ProgressBar

    lateinit var chosenSafe: Safe

    fun init() {
        progressCopy.isVisible = false
        if (!::chosenSafe.isInitialized) {
            throw NO_CHOSEN_SAFE_EXCEPTION
        }
        tblPwd.items.clear()
        tblPwd.items.addAll(chosenSafe.credentials.map { credential -> DisplayCredential(credential) })

        initUrlCol()
        initActionsCol()
        initPasswordCol()
        initTableClipboard()
        (tblPwd.columns[1] as TableColumn<DisplayCredential, String>).setCellValueFactory { SimpleStringProperty(it.value.credential.username) }


        for (col in tblPwd.columns) {
            col.prefWidthProperty()
                .bind(tblPwd.widthProperty().multiply(1 / tblPwd.columns.size.toDouble()).subtract(1))
        }
    }

    @FXML
    fun addCredential() {
        addOrUpdate()
    }

    private fun addOrUpdate(credential: Credential? = null) {
        val begTitle = if (credential == null) "Create" else "Update"
        val fxmlLoader = SceneManager.instance.getFxmlLoader("credential-management.fxml")

        val stage = Stage()
        stage.title = "$begTitle a credential"
        stage.scene = SceneManager.instance.load(fxmlLoader.load(), true)
        stage.show()

        val credentialManagerCtrl = fxmlLoader.getController<CredentialManagementController>()
        credentialManagerCtrl.credential = credential
        credentialManagerCtrl.chosenSafe = chosenSafe
        credentialManagerCtrl.init()

        stage.scene.window.setOnCloseRequest { updateView() } // When close button (OS managed)
        stage.scene.window.setOnHidden { updateView() } // When programmatically closed
    }

    fun updateView() {
        chosenSafe = SQLConnector.instance.getRepo(SafeRepositoryImpl::class.java).read(chosenSafe.id!!) as Safe
        init()
    }

    private fun initUrlCol() {
        val urlCol = tblPwd.columns[0] as TableColumn<DisplayCredential, String>

        urlCol.setCellFactory {
            object : TableCell<DisplayCredential, String>() {
                init {
                    setOnMouseClicked {
                        item?.let { KeetlinApplication.instance.getHostService().showDocument(item) }
                    }
                    setOnMouseEntered { item?.let { cursor = Cursor.HAND } }
                    styleClass.add("link")
                }

                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty) null else item ?: ""
                }
            }
        }
        urlCol.setCellValueFactory { SimpleStringProperty(it.value.credential.url) }
    }

    private fun initPasswordCol() {
        val pwdCol = tblPwd.columns[2] as TableColumn<DisplayCredential, String>
        pwdCol.setCellValueFactory { it.value.passwordProperty }
        pwdCol.setCellFactory {
            object : TableCell<DisplayCredential, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || tableRow.item == null) {
                        text = null
                    } else {
                        text = tableRow.item.passwordProperty.get()
                        tableRow.item.passwordProperty.addListener { _, _, _ -> updateItem(item, false) }
                    }
                }
            }
        }
    }

    private fun initActionsCol() {
        val actionCol = tblPwd.columns[3] as TableColumn<DisplayCredential, String>
        actionCol.setCellFactory {
            object : TableCell<DisplayCredential, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (!empty && tableRow.item != null) {
                        val iconLiteral = if (tableRow.item.passwordProperty.displayPwd) "mdi-eye-off" else "mdi-eye"
                        val displayPwdIcon = Icon(iconLiteral) {
                            tableRow.item.passwordProperty.displayPwd = !tableRow.item.passwordProperty.displayPwd
                            updateItem(item, false)
                        }
                        val updateCredential = Icon("mdi-settings") { addOrUpdate(tableRow.item.credential) }

                        val deleteCredential = Icon("mdi-delete") { delete(tableRow.item.credential) }

                        val hBox = HBox()
                        hBox.children.addAll(displayPwdIcon, updateCredential, deleteCredential)
                        hBox.alignment = Pos.CENTER

                        graphic = hBox
                    } else {
                        graphic = null
                    }
                }
            }
        }
    }

    private fun delete(credential: Credential) {
        val confirmDialog = Dialog<ButtonType>()
        confirmDialog.title = "Confirm deletion"
        confirmDialog.contentText = "Are you sure to delete this credential ?"

        val dp = DialogPane()
        dp.buttonTypes.addAll(ButtonType.YES, ButtonType.NO)

        confirmDialog.dialogPane = dp
        val result = confirmDialog.showAndWait()
        if (result.isPresent && result.get() == ButtonType.YES) {
            SQLConnector.instance.getRepo(CredentialRepositoryImpl::class.java).delete(credential.id!!)
            updateView()
        }
    }

    private fun initTableClipboard() {
        val copyPwdCmd = KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN)
        val copyUsernameCmd = KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN)

        tblPwd.setOnKeyPressed { event ->
            var keyCode: KeyCode? = null
            if (copyUsernameCmd.match(event)) {
                keyCode = KeyCode.N
            } else if (copyPwdCmd.match(event)) {
                keyCode = KeyCode.C
            }

            if (keyCode != null) copySelectionToClipboard(keyCode)
        }
    }

    private fun copySelectionToClipboard(keyCode: KeyCode?) {
        val selectedItem = tblPwd.selectionModel.selectedItem
        if (selectedItem != null) {
            val clipboard = Clipboard.getSystemClipboard()
            val content = ClipboardContent()

            var strContent: String? = null
            if (KeyCode.C == keyCode) {
                strContent = selectedItem.passwordProperty.getPlainPassword()
            } else if (KeyCode.N == keyCode) {
                strContent = selectedItem.credential.username
            }
            content.putString(strContent)
            clipboard.setContent(content)
            if (keyCode != null) handleTimeoutCopy(keyCode)
        }
    }

    private fun handleTimeoutCopy(keyCode: KeyCode) {
        progressCopy.isVisible = true
        val valueType = if (keyCode == KeyCode.C) "Password" else "Username"
        Notifications.create()
            .title("$valueType copied")
            .text("$maxSecCopy seconds remaining before it will be erased from clipboard")
            .show()
        curSecCopy = 0
        progressCopy.progress = .0
        val progressThread = Thread {
            for (i in 0..maxSecCopy) {
                val remain = maxSecCopy - curSecCopy
                println("$remain seconds remaining")
                Platform.runLater { progressCopy.progress = curSecCopy / maxSecCopy.toDouble() }

                Thread.sleep(1000)
                curSecCopy++
            }
            println("No more in clipboard")
            progressCopy.progress = .0
            Platform.runLater {
                copySelectionToClipboard(null)
                Notifications.create()
                    .title("$valueType erased from clipboard")
                    .show()
                progressCopy.isVisible = false
            }
        }
        progressThread.start()
    }
}
