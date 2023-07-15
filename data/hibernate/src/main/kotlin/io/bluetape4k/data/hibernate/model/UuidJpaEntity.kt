package io.bluetape4k.data.hibernate.model

import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuid
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.*

/**
 * [UUID] 수형의 Identifier를 가지는 JPA Entity의 추상 클래스입니다.
 */
@MappedSuperclass
abstract class UuidJpaEntity: AbstractJpaEntity<UUID>() {

    @field:Id
    override var id: UUID? = TimebasedUuid.nextUUID()

}
