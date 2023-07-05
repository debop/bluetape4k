package io.bluetape4k.data.hibernate

import io.bluetape4k.data.hibernate.model.JpaEntity
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.internal.SessionImpl
import java.io.Serializable
import java.sql.Connection
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery

fun EntityManager.currentSession(): Session = unwrap(Session::class.java)
fun EntityManager.asSession(): Session = unwrap(Session::class.java)

fun EntityManager.currentSessionImpl(): SessionImpl = unwrap(Session::class.java) as SessionImpl
fun EntityManager.asSessionImpl(): SessionImpl = unwrap(Session::class.java) as SessionImpl

fun EntityManager.sessionFactory(): SessionFactory = currentSession().sessionFactory


const val QUERY_DELETE_ALL = "delete from %s x"
const val QUERY_COUNT = "select n(*) from %s x"
const val QUERY_COUNT_HOLDER = "*"


private fun queryString(template: String, entityName: String): String = template.format(entityName)

fun <T> EntityManager.newQuery(resultClass: Class<T>): TypedQuery<T> {
    val builder = criteriaBuilder
    val query = builder.createQuery(resultClass)
    val root = query.from(resultClass)
    query.select(root)

    return createQuery(query)
}

fun <X> TypedQuery<X>.setPaging(firstResult: Int, maxResults: Int): TypedQuery<X> = apply {
    setFirstResult(firstResult)
    setMaxResults(maxResults)
}

/**
 * [entity]의 [property] 속성이 Proxy가 아닌, Load 된 Persistence Object 인지 판단합니다.
 *
 * @param entity 대상 entity
 * @param property entity manager 에 load 된 객체인지 검사할 속성
 * @return proxy 가 아니라면 true
 */
fun EntityManager.isLoaded(entity: Any?): Boolean =
    entity?.run { entityManagerFactory.persistenceUnitUtil.isLoaded(this) } ?: false

/**
 * [entity]의 [property] 속성이 Proxy가 아닌, Load 된 Persistence Object 인지 판단합니다.
 *
 * @param entity 대상 entity
 * @param property entity manager 에 load 된 객체인지 검사할 속성
 * @return proxy 가 아니라면 true
 */
fun EntityManager.isLoaded(entity: Any?, property: String): Boolean =
    entity?.run { entityManagerFactory.persistenceUnitUtil.isLoaded(this, property) } ?: false


fun <T: JpaEntity<*>> EntityManager.save(entity: T): T {
    return if (entity.isPersisted && !contains(entity)) {
        merge(entity)
    } else {
        persist(entity)
        entity
    }
}

fun <T: JpaEntity<*>> EntityManager.delete(entity: T) {
    if (entity.isPersisted) {
        if (!contains(entity)) {
            remove(merge(entity))
        } else {
            remove(entity)
        }
    }
}

inline fun <reified T> EntityManager.deleteById(id: Serializable) {
    val entity = this.getReference(T::class.java, id)
    entity?.let { remove(it) }
}

inline fun <reified T: Any> EntityManager.findAs(id: Serializable): T? = find(T::class.java, id)

inline fun <reified T> EntityManager.findOne(id: Serializable): T? = find(T::class.java, id)

inline fun <reified T> EntityManager.exists(id: Serializable): Boolean = find(T::class.java, id) != null

fun <T> EntityManager.findAll(clazz: Class<T>): List<T> {
    return newQuery(clazz).resultList
}

/**
 * 현 [EntityManager] 가 사용하는 [Connection] 을 가져옵니다.
 */
fun <T> EntityManager.currentConnection(): Connection {
    return currentSessionImpl()
        .jdbcCoordinator
        .logicalConnection
        .physicalConnection
}
