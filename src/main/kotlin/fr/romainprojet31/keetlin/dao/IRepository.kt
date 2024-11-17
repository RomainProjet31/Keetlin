package fr.romainprojet31.keetlin.dao

import fr.romainprojet31.keetlin.model.BModel

interface ReadOnlyRepository<out T : BModel> {
    fun all(): ArrayList<@UnsafeVariance T>
    fun read(id: Long): T
}

interface WriteOnlyRepository<in T : BModel> {
    fun save(t: T): @UnsafeVariance T
    fun delete(id: Long): Boolean
    fun commit() {
        SQLConnector.instance.connection.commit()
    }
}

interface IRepository<T : BModel> : ReadOnlyRepository<T>, WriteOnlyRepository<T>
