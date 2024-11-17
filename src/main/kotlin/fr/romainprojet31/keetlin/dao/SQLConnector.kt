package fr.romainprojet31.keetlin.dao

import fr.romainprojet31.keetlin.KeetlinApplication
import fr.romainprojet31.keetlin.model.BModel
import java.sql.Connection
import java.sql.DriverManager

class SQLConnector {
    private val repositories: ArrayList<IRepository<out BModel>> = arrayListOf()
    lateinit var connection: Connection
    private val dbPath = KeetlinApplication::class.java.getResource("db/keetlin.sqlite")

    init {
        checkConnection()
        repositories.add(SafeRepositoryImpl())
        repositories.add(CredentialRepositoryImpl())
    }

    fun <T : Any> getRepo(clazz: Class<T>): IRepository<out BModel> {
        checkConnection()
        return repositories
            .stream()
            .filter { r -> r.javaClass == clazz }
            .findFirst()
            .orElseThrow()
    }

    private fun checkConnection() {
        if (!::connection.isInitialized || connection.isClosed) {
            try {
                Class.forName("org.sqlite.JDBC")
                connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
                println("Connexion établie avec succès à la base de données SQLite.")
            } catch (e: Exception) {
                println("Erreur lors de la connexion à la base de données : ${e.message}")
            }
        }
    }

    companion object {
        val instance: SQLConnector = SQLConnector()
    }
}

