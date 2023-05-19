package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
data class City(
    @field:Column(nullable = false)
    open var name: String,

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0L,
): java.io.Serializable
