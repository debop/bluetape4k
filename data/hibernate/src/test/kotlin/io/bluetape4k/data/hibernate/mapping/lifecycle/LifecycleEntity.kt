package io.bluetape4k.data.hibernate.mapping.lifecycle

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.validation.constraints.NotBlank

/**
 * AuditingEntityListener 를 등록하면 @CreatedDate, @LastModifiedDate 가 자동으로 채워진다.
 * 단 SpringBoot 환경에 `@EnableJpaAuditing(modifyOnCreate = true)` 설정이 필요하다.
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Access(AccessType.FIELD)
class LifecycleEntity private constructor(
    @Column(nullable = false)
    @NotBlank
    var name: String
) : IntJpaEntity() {

    companion object {
        @JvmStatic
        operator fun invoke(name: String): LifecycleEntity {
            name.requireNotBlank("name")
            return LifecycleEntity(name)
        }
    }

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(insertable = false)
    var updatedAt: LocalDateTime? = null


    override fun equalProperties(other: Any): Boolean {
        return other is LifecycleEntity && name == other.name
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
