package fr.romainprojet31.keetlin.model

import fr.romainprojet31.keetlin.constants.WORKLOAD
import org.mindrot.jbcrypt.BCrypt

class Safe(id: Long?, var name: String, masterPassword: String) : BModel(id) {
    var credentials: ArrayList<Credential> = ArrayList()
        private set

    var masterPassword: String = masterPassword
        set(value) {
            field = BCrypt.hashpw(value, BCrypt.gensalt(WORKLOAD))
        }

    constructor(name: String, masterPassword: String) : this(null, name, "") {
        this.masterPassword = masterPassword
    }

    fun checkPwd(plainPwd: String): Boolean {
        return BCrypt.checkpw(plainPwd, masterPassword)
    }
}