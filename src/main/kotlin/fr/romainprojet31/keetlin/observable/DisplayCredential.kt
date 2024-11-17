package fr.romainprojet31.keetlin.observable

import fr.romainprojet31.keetlin.model.Credential
import javafx.beans.property.SimpleBooleanProperty

/**
 * Credential to display in Nodes as TableView
 */
class DisplayCredential(credential: Credential) {
    var credential = credential
        private set
    var passwordProperty: PasswordProperty = PasswordProperty(credential.password.encryptedPassword)
        private set
}