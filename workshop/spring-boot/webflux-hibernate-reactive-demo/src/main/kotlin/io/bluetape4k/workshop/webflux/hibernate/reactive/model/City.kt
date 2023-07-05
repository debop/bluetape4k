package io.bluetape4k.workshop.webflux.hibernate.reactive.model

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.hibernate.model.LongJpaEntity
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
@Access(AccessType.FIELD)
class City: LongJpaEntity() {

    companion object {
        @JvmStatic
        operator fun invoke(name: String): City {
            name.requireNotBlank("name")
            return City().apply {
                this.name = name
            }
        }
    }

    @Column(nullable = false, length = 255)
    var name: String = ""

    override fun equalProperties(other: Any): Boolean =
        other is City && name == other.name

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
