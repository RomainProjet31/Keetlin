package fr.romainprojet31.keetlin

import fr.romainprojet31.keetlin.view.SceneManager
import javafx.application.Application
import javafx.application.HostServices
import javafx.stage.Stage

object WindowConstant {
    const val WIDTH: Double = 600.0
    const val HEIGHT: Double = 400.0
    const val NAME: String = "KeetlinPass"
}

class KeetlinApplication : Application() {
    companion object {
        lateinit var instance: KeetlinApplication
            private set
    }

    override fun start(stage: Stage) {
        instance = this
        stage.title = WindowConstant.NAME
        stage.scene = SceneManager.instance.load("authentication.fxml")
        stage.show()
    }

    fun getHostService(): HostServices {
        return hostServices
    }
}

fun main() {
    Application.launch(KeetlinApplication::class.java)
}