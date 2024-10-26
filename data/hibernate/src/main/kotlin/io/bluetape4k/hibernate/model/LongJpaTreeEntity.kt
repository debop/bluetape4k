//package io.bluetape4k.hibernate.model
//
//import jakarta.persistence.GeneratedValue
//import jakarta.persistence.GenerationType
//import jakarta.persistence.Id
//import jakarta.persistence.MappedSuperclass
//
///**
// * Long 수형의 Identifier를 가지는 [JpaTreeEntity]의 추상 클래스입니다.
// *
// * @param T
// * @constructor Create empty Long jpa tree entity
// */
//@MappedSuperclass
//abstract class LongJpaTreeEntity<T: LongJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Long>() {
//
//    @field:Id
//    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
//    override var id: Long? = null
//
//}
