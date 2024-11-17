package fr.romainprojet31.keetlin.dao

import fr.romainprojet31.keetlin.model.Credential
import java.sql.PreparedStatement

class CredentialRepositoryImpl : IRepository<Credential> {
    override fun all(): ArrayList<Credential> {
        val sql = "SELECT id, url, username, password, safe_id FROM Credential"
        val rs = SQLConnector.instance.connection.createStatement().executeQuery(sql)
        val safes = ArrayList<Credential>()
        while (rs.next()) {
            val id = rs.getLong(1)
            val url = rs.getString(2)
            val username = rs.getString(3)
            val password = rs.getString(4)
            val safeId = rs.getLong(5)
            safes.add(Credential(id, url, username, password, safeId))
        }
        return safes
    }

    fun allBySafeId(safeId: Long): ArrayList<Credential> {
        val sql = "SELECT id, url, username, password FROM Credential WHERE safe_id = ?"
        val stmt: PreparedStatement = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setLong(1, safeId)

        val rs = stmt.executeQuery()
        val credentials = ArrayList<Credential>()
        while (rs.next()) {
            val id = rs.getLong(1)
            val url = rs.getString(2)
            val username = rs.getString(3)
            val password = rs.getString(4)
            credentials.add(Credential(id, url, username, password, safeId))
        }
        return credentials
    }

    override fun read(id: Long): Credential {
        TODO("Not yet implemented")
    }

    override fun save(t: Credential): Credential {
        return if (t.id == null) insert(t) else update(t)
    }

    private fun insert(credential: Credential): Credential {
        val sql = "INSERT INTO CREDENTIAL (url, username, password, safe_id) values (?,?,?,?)"
        val stmt: PreparedStatement = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setString(1, credential.url)
        stmt.setString(2, credential.username)
        stmt.setString(3, credential.password.encryptedPassword)
        stmt.setLong(4, credential.safeId)
        if (stmt.executeUpdate() == 1) {
            credential.id = all().filter { c ->
                c.username == credential.username
                        && c.password.encryptedPassword == credential.password.encryptedPassword
                        && c.url == credential.url
            }[0].id
        }
        return credential
    }

    private fun update(credential: Credential): Credential {
        assert(credential.id != null) { "Cannot update an out-of-sync Model" }

        val sql = "UPDATE CREDENTIAL SET url=?, username=?, password=? WHERE id=?"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setString(1, credential.url)
        stmt.setString(2, credential.username)
        stmt.setString(3, credential.password.encryptedPassword)
        stmt.setLong(4, credential.id!!)

        if (stmt.executeUpdate() == 1) {
            credential.id = all().filter { c ->
                c.username == credential.username
                        && c.password.encryptedPassword == credential.password.encryptedPassword
                        && c.url == credential.url
            }[0].id
        }
        return credential
    }

    override fun delete(id: Long): Boolean {
        val sql = "DELETE FROM CREDENTIAL WHERE ID = ?"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setLong(1, id)
        return stmt.executeUpdate() == 1
    }
}