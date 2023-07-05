package io.bluetape4k.data.hibernate.mapping.compositeid

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.AbstractPersistenceObject
import java.io.Serializable
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity

@Embeddable
@Access(AccessType.FIELD)
data class EmbeddableCarId(
    @Column(nullable = false, length = 64)
    var brand: String,

    @Column(nullable = false)
    var carYear: Int,
): Serializable

@Entity
@Access(AccessType.FIELD)
class EmbeddedIdCar(
    @EmbeddedId
    val id: EmbeddableCarId,
): AbstractPersistenceObject() {

    var serialNo: String? = null

    override fun equalProperties(other: Any): Boolean {
        return other is EmbeddedIdCar && other.id == id
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("id", id)
    }
}
