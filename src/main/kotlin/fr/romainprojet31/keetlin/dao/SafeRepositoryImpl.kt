package fr.romainprojet31.keetlin.dao

import fr.romainprojet31.keetlin.constants.NO_SAFE_FOUND_IN_DB
import fr.romainprojet31.keetlin.model.Safe

class SafeRepositoryImpl : IRepository<Safe> {
    override fun all(): ArrayList<Safe> {
        val sql = "SELECT id, name, master_password FROM Safe"
        val rs = SQLConnector.instance.connection.createStatement().executeQuery(sql)
        val safes = ArrayList<Safe>()
        while (rs.next()) {
            val id = rs.getLong(1)
            val name = rs.getString(2)
            val masterPwd = rs.getString(3)
            val currentSafe = Safe(id, name, masterPwd)
            currentSafe.credentials.addAll(
                (SQLConnector.instance.getRepo(CredentialRepositoryImpl::class.java) as CredentialRepositoryImpl)
                    .allBySafeId(currentSafe.id!!)
            )
            safes.add(currentSafe)
        }
        return safes
    }

    override fun read(id: Long): Safe {
        val sql = "SELECT name, master_password FROM Safe WHERE id = ?"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setLong(1, id)
        val rs = stmt.executeQuery()
        if (rs.next()) {
            val foundSafe = Safe(id, rs.getString(1), rs.getString(2))
            foundSafe.credentials.addAll(
                (SQLConnector.instance.getRepo(CredentialRepositoryImpl::class.java) as CredentialRepositoryImpl)
                    .allBySafeId(id)
            )
            return foundSafe
        } else {
            throw NO_SAFE_FOUND_IN_DB
        }
    }

    override fun save(t: Safe): Safe {
        return if (t.id != null) {
            update(t)
        } else {
            create(t)
        }
    }

    private fun create(safe: Safe): Safe {
        val sql = "INSERT INTO SAFE (name, master_password) values (?,?)"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setString(1, safe.name)
        stmt.setString(2, safe.masterPassword)
        if (stmt.executeUpdate() == 1) {
            return all()
                .stream()
                .filter { s -> s.name == safe.name && safe.masterPassword == s.masterPassword }
                .findFirst()
                .orElseThrow()
        } else {
            throw NO_SAFE_FOUND_IN_DB
        }
    }

    private fun update(safe: Safe): Safe {
        val sql = "UPDATE SAFE SET name=?, master_password=? WHERE id=?"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setString(1, safe.name)
        stmt.setString(2, safe.masterPassword)
        stmt.setLong(3, safe.id!!)
        if (stmt.executeUpdate() == 1) {
            return safe
        } else {
            throw NO_SAFE_FOUND_IN_DB
        }
    }

    override fun delete(id: Long): Boolean {
        val sql = "DELETE FROM SAFE WHERE id=?"
        val stmt = SQLConnector.instance.connection.prepareStatement(sql)
        stmt.setLong(1, id)
        return stmt.executeUpdate() == 1
    }

}