package io.bluetape4k.data.hibernate.model

import io.bluetape4k.utils.idgenerators.uuid.TimebasedUuid
import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * [UUID] 수형의 Identifier를 가지는 JPA Entity의 추상 클래스입니다.
 */
@MappedSuperclass
abstract class UuidJpaEntity: AbstractJpaEntity<UUID>() {

    @Id
    override var id: UUID? = TimebasedUuid.nextUUID()

}
