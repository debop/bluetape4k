package io.bluetape4k.collections.enhanced

import io.bluetape4k.collections.AbstractCollectionDecorator
import io.bluetape4k.logging.KLogging
import java.util.function.Predicate

/**
 *
 * `keySelector`로 선정된 key에 의해 요소를 인덱싱하는 컬렉션입니다.
 *
 * @param K the type of the key
 * @param E the type of the elements
 * @property keySelector 인덱싱에 사용될 key를 선택하는 함수
 * @property uniqueIndex 인덱스가 유일해야 하는지 여부
 */
class IndexedCollection<K, E> private constructor(
    decorated: MutableCollection<E>,
    private val keySelector: (E) -> K,
    private val uniqueIndex: Boolean,
): AbstractCollectionDecorator<E>(decorated) {

    companion object: KLogging() {

        @JvmStatic
        operator fun <K, E> invoke(
            collection: MutableCollection<E>,
            uniqueIndex: Boolean = true,
            keySelector: (E) -> K,
        ): IndexedCollection<K, E> {
            return IndexedCollection(collection, keySelector, uniqueIndex)
        }

        fun <K, E> uniqueIndexed(
            collection: MutableCollection<E>,
            keySelector: (E) -> K,
        ): IndexedCollection<K, E> {
            return invoke(collection, true, keySelector)
        }

        fun <K, E> nonUniqueIndexed(
            collection: MutableCollection<E>,
            keySelector: (E) -> K,
        ): IndexedCollection<K, E> {
            return invoke(collection, false, keySelector)
        }
    }

    private val index = mutableMapOf<K, MutableList<E>>()

    init {
        reindex()
    }

    override fun add(element: E): Boolean {
        val added = super.add(element)
        if (added) {
            addToIndex(element)
        }
        return added
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var changed = false
        elements.forEach {
            changed = changed or add(it)
        }
        return changed
    }

    override fun clear() {
        super.clear()
        index.clear()
    }

    override fun contains(element: E): Boolean {
        return index.containsKey(keySelector(element))
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return elements.all { contains(it) }
    }

    operator fun get(key: K): E? {
        return index[key]?.firstOrNull()
    }

    fun values(key: K): MutableCollection<E> {
        return index[key] ?: mutableListOf()
    }

    override fun remove(element: E): Boolean {
        val removed = super.remove(element)
        if (removed) {
            removeFromIndex(element)
        }
        return removed
    }

    override fun removeIf(filter: Predicate<in E>): Boolean {
        var changed = false
        val iter = iterator()
        while (iter.hasNext()) {
            if (filter.test(iter.next())) {
                iter.remove()
                changed = true
            }
        }
        if (changed) {
            reindex()
        }
        return changed
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var changed = false
        elements.forEach {
            changed = changed or remove(it)
        }
        return changed
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val changed = super.retainAll(elements.toSet())
        if (changed) {
            reindex()
        }
        return changed
    }

    fun reindex() {
        index.clear()
        decorated.forEach {
            addToIndex(it)
        }
    }

    private fun addToIndex(element: E) {
        val key: K = keySelector(element)
        if (uniqueIndex && index.containsKey(key)) {
            throw IllegalArgumentException("Duplicate key in uniquely indexed collection: key=$key, element=$element")
        }
        index.computeIfAbsent(key) { mutableListOf() }.add(element)
    }

    private fun removeFromIndex(element: E) {
        val key = keySelector(element)
        index.remove(key)
    }
}
