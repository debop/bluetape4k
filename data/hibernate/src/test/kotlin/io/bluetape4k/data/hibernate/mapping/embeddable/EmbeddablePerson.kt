package io.bluetape4k.data.hibernate.mapping.embeddable

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.hibernate.converters.RC4StringConverter
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import io.bluetape4k.support.hashOf
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(
    indexes = [
        Index(name = "idx_embeddable_person_username", columnList = "user_id, password", unique = true),
        Index(name = "idx_embeddable_person_email", columnList = "email", unique = true)
    ]
)
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class EmbeddablePerson private constructor(
    @NotBlank
    @Column(name = "user_id", nullable = false, length = 32, unique = true)
    var userId: String,

    @NotBlank
    @Convert(converter = RC4StringConverter::class)
    var password: String
): IntJpaEntity() {

    companion object {
        @JvmStatic
        operator fun invoke(username: String, password: String): EmbeddablePerson {
            username.requireNotBlank("username")
            password.requireNotBlank("password")
            return EmbeddablePerson(username, password)
        }
    }

    @Column(nullable = false, unique = true, length = 64)
    var email: String = ""

    var active: Boolean = true

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "home_street", length = 128)),
        AttributeOverride(name = "city", column = Column(name = "home_city", length = 24)),
        AttributeOverride(name = "zipcode", column = Column(name = "home_zipcode", length = 8))
    )
    var homeAddress: EmbeddableAddress? = null
    // 위와 같이 nullable 을 사용하는 방식도 있고, EmbeddableAddress의 기본값을 적용해도 된다.
    // val homeAddress = EmbeddableAddress()

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "office_street", length = 128)),
        AttributeOverride(name = "city", column = Column(name = "office_city", length = 24)),
        AttributeOverride(name = "zipcode", column = Column(name = "office_zipcode", length = 8))
    )
    var officeAddress: EmbeddableAddress? = null // = EmbeddableAddress()

    override fun equalProperties(other: Any): Boolean {
        return other is EmbeddablePerson &&
            userId == other.userId &&
            email == other.email
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: hashOf(userId, email)
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("userId", userId)
            .add("email", email)
    }
}
