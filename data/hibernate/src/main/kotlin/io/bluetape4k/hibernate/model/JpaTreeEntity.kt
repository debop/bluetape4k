package io.bluetape4k.hibernate.model

/**
 * Self reference 를 가지는 Tree 구조의 엔티니를 표현합니다.
 */
interface JpaTreeEntity<T>: PersistenceObject where T: JpaTreeEntity<T>, T: Any {

    var parent: T?

    val children: MutableSet<T>

    // Node 위치를 나타내도록 합니다. 거의 사용하지 않아 삭제할 예정입니다.
    // val nodePosition: TreeNodePosition

    @Suppress("UNCHECKED_CAST")
    fun addChildren(vararg childs: T) {
        childs.forEach {
            if (children.add(it)) {
                it.parent = this as T
            }
        }
    }

    fun removeChildren(vararg childs: T) {
        childs.forEach {
            if (children.remove(it)) {
                it.parent = null
            }
        }
    }
}
