package io.bluetape4k.collections.enhanced

import io.bluetape4k.collections.asMutableIterator
import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.stream.asStream
import io.bluetape4k.logging.KLogging
import org.eclipse.collections.impl.EmptyIterator
import java.io.Serializable
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * 여러개의 [MutableCollection]을 하나의 [MutableCollection]처럼 제공하는 클래스입니다.
 *
 * @param E
 * @constructor Create empty Composite collection
 */
class CompositeCollection<E> private constructor(): MutableCollection<E>, Serializable {

    companion object: KLogging() {
        operator fun <E> invoke(vararg collections: MutableCollection<E>): CompositeCollection<E> =
            CompositeCollection<E>().apply {
                addComposited(*collections)
            }
    }

    private val all = fastListOf<MutableCollection<E>>()

    var mutator: CollectionMutator<E>? = null

    override val size: Int
        get() = all.sumOf { it.size }

    val collections: List<MutableCollection<E>> get() = all.toList()

    override fun clear() {
        all.forEach { it.clear() }
    }

    override fun add(element: E): Boolean {
        if (mutator == null) {
            throw UnsupportedOperationException(
                "add() is not supported on CompositeCollection without a CollectionMutator strategy"
            )
        }
        return mutator!!.add(this, all, element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if (mutator == null) {
            throw UnsupportedOperationException(
                "addAll() is not supported on CompositeCollection without a CollectionMutator strategy"
            )
        }
        return mutator!!.addAll(this, all, elements)
    }

    override fun isEmpty(): Boolean {
        return all.all { it.isEmpty() }
    }

    override fun iterator(): MutableIterator<E> {
        if (all.isEmpty) {
            return EmptyIterator.getInstance()
        }

        return sequence {
            all.forEach { coll ->
                coll.forEach { item ->
                    yield(item)
                }
            }
        }
            .iterator()
            .asMutableIterator()
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        if (elements.isEmpty()) {
            return false
        }
        var changed = false
        all.forEach { coll ->
            changed = changed or coll.retainAll(elements.toSet())
        }
        return changed
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        if (elements.isEmpty()) {
            return false
        }
        var changed = false
        all.forEach { coll ->
            changed = changed or coll.removeAll(elements.toSet())
        }
        return changed
    }

    override fun remove(element: E): Boolean {
        if (mutator == null) {
            throw UnsupportedOperationException(
                "remove() is not supported on CompositeCollection without a CollectionMutator strategy"
            )
        }
        return mutator!!.remove(this, all, element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        if (elements.isEmpty()) {
            return false
        }
        return elements.all { contains(it) }
    }

    override fun contains(element: E): Boolean {
        return all.any { it.contains(element) }
    }

    override fun removeIf(filter: Predicate<in E>): Boolean {
        var changed = false
        all.forEach { coll ->
            changed = changed or coll.removeIf(filter)
        }
        return changed
    }

    override fun forEach(action: Consumer<in E>?) {
        all.forEach { coll ->
            coll.forEach(action)
        }
    }

    override fun stream(): Stream<E> {
        return all.flatMap { it.asSequence() }.asStream()
    }

    override fun parallelStream(): Stream<E> {
        return stream().parallel()
    }

    fun addComposited(collection: MutableCollection<E>) {
        all.add(collection)
    }

    fun addComposited(vararg collections: MutableCollection<E>) {
        collections.forEach {
            addComposited(it)
        }
    }

    fun removeComposited(collection: MutableCollection<E>) {
        all.remove(collection)
    }

    /**
     * Pluggable strategy to handle changes to the composite.
     *
     * @param <E> the element being held in the collection
     */
    interface CollectionMutator<E>: Serializable {

        /**
         * Called when an object is to be added to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param element  the object being added
         * @return true if the collection is changed
         */
        fun add(
            composite: CompositeCollection<E>,
            collections: List<MutableCollection<E>>,
            element: Any?,
        ): Boolean

        /**
         * Called when a collection is to be added to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param elements  the collection being added
         * @return true if the collection is changed
         */
        fun addAll(
            composite: CompositeCollection<E>,
            collections: List<MutableCollection<E>>,
            elements: Collection<E>,
        ): Boolean

        /**
         * Called when an object is to be removed to the composite.
         *
         * @param composite  the CompositeCollection being changed
         * @param collections  all of the Collection instances in this CompositeCollection
         * @param element  the object being removed
         * @return true if the collection is changed
         */
        fun remove(
            composite: CompositeCollection<E>,
            collections: List<MutableCollection<E>>,
            element: Any?,
        ): Boolean
    }
}
