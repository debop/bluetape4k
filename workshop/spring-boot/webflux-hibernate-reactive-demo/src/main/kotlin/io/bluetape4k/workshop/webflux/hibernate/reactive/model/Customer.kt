package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.hibernate.model.LongJpaEntity
import io.bluetape4k.support.requireNotBlank
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.FetchProfile

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
class Customer: LongJpaEntity() {

    companion object {
        @JvmStatic
        operator fun invoke(name: String): Customer {
            name.requireNotBlank("name")
            return Customer().apply {
                this.name = name
            }
        }
    }

    @Column(nullable = false, length = 255)
    var name: String = ""

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = true,
        cascade = [CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE]
    )
    var city: City? = null

    override fun equalProperties(other: Any): Boolean =
        other is Customer && name == other.name

    override fun equals(other: Any?): Boolean =
        other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
            .add("city", city?.name)
    }
}
