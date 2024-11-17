module fr.romainprojet.keetlin {
    requires kotlin.stdlib;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires jasypt;
    requires org.controlsfx.controls;

    exports fr.romainprojet31.keetlin;
    exports fr.romainprojet31.keetlin.controllers;
    opens fr.romainprojet31.keetlin to javafx.graphics;
    opens fr.romainprojet31.keetlin.controllers to javafx.fxml;
}