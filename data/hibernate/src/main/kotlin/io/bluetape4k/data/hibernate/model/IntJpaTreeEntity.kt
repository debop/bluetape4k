package io.bluetape4k.data.hibernate.model

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class IntJpaTreeEntity<T: IntJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Int>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null

}