package io.bluetape4k.data.hibernate.model

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class LongJpaTreeEntity<T: LongJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

}
