package io.bluetape4k.data.hibernate.model


/**
 * Self reference 를 가지는 Tree 구조의 엔티티인 [JpaTreeEntity]의 추상 클래스입니다.
 *
 * @param T  entity type
 * @param ID identifier type
 */
// FIXME: Kapt 작업에서 예외가 발생한다.
//@MappedSuperclass
//abstract class AbstractJpaTreeEntity<T: JpaTreeEntity<T>, ID: Serializable>
//    : AbstractJpaEntity<ID>(), JpaTreeEntity<T> {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_id")
//    override var parent: T? = null
//
//    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    override val children: MutableSet<T> = mutableSetOf()
//
//}
