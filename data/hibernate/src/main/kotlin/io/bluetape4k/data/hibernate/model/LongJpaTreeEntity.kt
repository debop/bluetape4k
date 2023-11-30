package io.bluetape4k.data.hibernate.model

/**
 * Long 수형의 Identifier를 가지는 [JpaTreeEntity]의 추상 클래스입니다.
 *
 * @param T
 * @constructor Create empty Long jpa tree entity
 */
// FIXME: Kapt 작업에서 예외가 발생한다.
/*
@MappedSuperclass
abstract class LongJpaTreeEntity<T: LongJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Long>() {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null
}
*/
