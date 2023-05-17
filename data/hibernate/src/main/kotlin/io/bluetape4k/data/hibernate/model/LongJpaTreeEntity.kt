package io.bluetape4k.data.hibernate.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * Long 수형의 Identifier를 가지는 [JpaTreeEntity]의 추상 클래스입니다.
 *
 * @param T
 * @constructor Create empty Long jpa tree entity
 */
@MappedSuperclass
abstract class LongJpaTreeEntity<T: LongJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null
}
