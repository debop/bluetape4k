package io.bluetape4k.data.hibernate.model

import java.io.Serializable
import javax.persistence.CascadeType
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany


/**
 * Self reference 를 가지는 Tree 구조의 엔티티인 [JpaTreeEntity]의 추상 클래스입니다.
 *
 * @param T  entity type
 * @param ID identifier type
 */
@MappedSuperclass
abstract class AbstractJpaTreeEntity<T: JpaTreeEntity<T>, ID: Serializable>
    : AbstractJpaEntity<ID>(), JpaTreeEntity<T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    override var parent: T? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    override val children: MutableSet<T> = mutableSetOf()

}
