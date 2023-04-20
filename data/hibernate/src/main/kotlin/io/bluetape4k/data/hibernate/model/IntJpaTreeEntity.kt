package io.bluetape4k.data.hibernate.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class IntJpaTreeEntity<T: IntJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Int>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int? = null

}
