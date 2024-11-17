package fr.romainprojet31.keetlin.view

import javafx.scene.control.Label
import javafx.stage.Popup
import javafx.stage.Stage


class KeetlinPopup(val label: Label) : Popup() {

    init {
        isAutoFix = true
        isAutoHide = true
        isHideOnEscape = true
        label.setOnMouseReleased { hide() }
        label.stylesheets.add("")
        content.add(label)
    }

    public fun show(stage: Stage) {
        x = (stage.x + stage.width / 2 - width / 2)
        y = (stage.y + stage.height / 2 - height / 2)
        super.show()
    }
}