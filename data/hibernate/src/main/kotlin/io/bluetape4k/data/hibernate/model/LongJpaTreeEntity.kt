package io.bluetape4k.data.hibernate.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class LongJpaTreeEntity<T: LongJpaTreeEntity<T>>: AbstractJpaTreeEntity<T, Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

}
