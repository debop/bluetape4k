package io.bluetape4k.data.hibernate.model

import java.io.Serializable

/**
 * JPA Entity interface
 */
interface JpaEntity<ID: Serializable>: PersistenceObject {

    /**
     * Entity identifier
     */
    var id: ID?

    val identifier: ID get() = id!!
}
