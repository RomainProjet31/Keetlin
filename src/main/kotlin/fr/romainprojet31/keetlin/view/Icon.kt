package fr.romainprojet31.keetlin.view

import org.kordamp.ikonli.javafx.FontIcon

/**
 * Icon base on Ikonli to better manage their appearance and callback
 * @see <a href="https://kordamp.org/ikonli/cheat-sheet-materialdesign.html">Ikonli cheat sheet MaterialDesign</a>
 */
class Icon(iconNameLiteral: String, callback: () -> Unit) : FontIcon() {

    init {
        iconLiteral = iconNameLiteral
        styleClass.add("ikonli-font")
        setOnMouseClicked { callback.invoke() }
        iconSize.value = iconSize.value * 2
    }
}