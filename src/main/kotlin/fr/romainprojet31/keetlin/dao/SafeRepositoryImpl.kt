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
        // Implémentation de la méthode
        return t
    }

    override fun delete(id: Long): Boolean {
        // Implémentation de la méthode
        return true
    }

}