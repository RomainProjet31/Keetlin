package fr.romainprojet31.keetlin.view

import fr.romainprojet31.keetlin.KeetlinApplication
import fr.romainprojet31.keetlin.WindowConstant
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.kordamp.bootstrapfx.BootstrapFX

class SceneManager {
    companion object {
        val instance: SceneManager = SceneManager()
    }

    private lateinit var scene: Scene

    fun load(root: Parent, newWindow: Boolean = false): Scene {
        if (newWindow) {
            return Scene(root, WindowConstant.WIDTH, WindowConstant.HEIGHT)
        } else if (!::scene.isInitialized) {
            scene = Scene(root, WindowConstant.WIDTH, WindowConstant.HEIGHT)
        } else {
            scene.root = root
        }
        root.autosize()
        return scene
    }

    fun load(fxmlPath: String): Scene {
        val scene: Scene = load(getFxmlLoader(fxmlPath).load<Parent>())
        scene.stylesheets.add(BootstrapFX.bootstrapFXStylesheet())
        return scene
    }

    fun getFxmlLoader(fxmlPath: String): FXMLLoader {
        return FXMLLoader(KeetlinApplication::class.java.getResource(fxmlPath))
    }
}