package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.FetchProfile
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

// TODO: LongJpaEntity 를 상속받는 것으로 변경하자 
@Entity
@Access(AccessType.FIELD)
// NOTE: city 를 lazy 로 얻기 위해서는 @FetchProfile 을 이용해야 합니다.
// NOTE: 단 StatelessSession에서는 지원하지 않습니다
@FetchProfile(
    name = "withCity",
    fetchOverrides = [
        FetchProfile.FetchOverride(entity = Customer::class, association = "city", mode = FetchMode.JOIN)
    ]
)
data class Customer(
    @field:Column(nullable = false)
    open var name: String,
): java.io.Serializable {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0L

    @field:ManyToOne(fetch = FetchType.LAZY, optional = true)
    @field:JoinColumn(name = "city_id")
    open var city: City? = null
}
