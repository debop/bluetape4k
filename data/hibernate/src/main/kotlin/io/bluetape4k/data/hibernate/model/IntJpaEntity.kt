package io.bluetape4k.data.hibernate.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * Int 수형의 Identifier를 가지는 JPA Entity의 추상 클래스입니다.
 */
@MappedSuperclass
abstract class IntJpaEntity: AbstractJpaEntity<Int>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null

}
