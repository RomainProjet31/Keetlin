package fr.romainprojet31.keetlin.view

import fr.romainprojet31.keetlin.dao.SQLConnector
import fr.romainprojet31.keetlin.dao.SafeRepositoryImpl
import fr.romainprojet31.keetlin.model.Safe
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.kordamp.bootstrapfx.BootstrapFX
import java.net.URL
import java.util.*

class SafeView(var safe: Safe?) : Pane(), Initializable {
    private val nameTxt = TextField()
    private val pwdTxt = PasswordField()
    private val isValid = SimpleBooleanProperty()

    constructor() : this(null)

    init {
        val submit = Button("Save")
        submit.setOnAction { onSave() }
        submit.disableProperty().bind(!isValid)

        val lblExplanation =
            Label("The password must be a set of digit and letter and be at least composed of 8 characters")


        val vbox = VBox()
        vbox.alignment = Pos.CENTER

        val actionGroup = VBox(submit, lblExplanation)
        actionGroup.alignment = Pos.CENTER

        nameTxt.promptText = "Enter the safe name"
        nameTxt.alignment = Pos.CENTER

        pwdTxt.promptText = "Enter the safe password"
        pwdTxt.alignment = Pos.CENTER

        nameTxt.prefWidthProperty().bind(widthProperty().subtract(10))
        pwdTxt.prefWidthProperty().bind(widthProperty().subtract(10))

        vbox.children.addAll(nameTxt, pwdTxt, actionGroup)
        vbox.spacingProperty().bind(heightProperty().divide(4))
        children.add(vbox)

        pwdTxt.setOnKeyReleased {
            isValid.set(isPwdOk() && !nameTxt.text.isNullOrEmpty())
        }
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        stylesheets.add(BootstrapFX.bootstrapFXStylesheet())
        stylesheets.add(javaClass.getResource("./style.css").toExternalForm())
    }

    private fun onSave() {
        println("Attention")
        if (isValid.get()) {
            val safeToSave = Safe(nameTxt.text, pwdTxt.text)
            safeToSave.id =
                (SQLConnector.instance.getRepo(SafeRepositoryImpl::class.java) as SafeRepositoryImpl).save(safeToSave).id
        }
    }

    private fun isPwdOk(): Boolean {
        val lengthOk = pwdTxt.text.length >= 8
        var numberOk = false
        var charOk = false
        for (chr in pwdTxt.text) {
            if (chr.isDigit()) {
                numberOk = true
            } else if (chr.isLetter()) {
                charOk = true
            }
        }
        return lengthOk && numberOk && charOk
    }
}