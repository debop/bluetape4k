package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

// TODO: LongJpaEntity 를 상속받는 것으로 변경하자 
@Entity
@Access(AccessType.FIELD)
data class City(
    @field:Column(nullable = false)
    open var name: String,
): java.io.Serializable {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0L
}
