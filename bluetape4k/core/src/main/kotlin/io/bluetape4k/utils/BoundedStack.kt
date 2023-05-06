package io.bluetape4k.utils

import io.bluetape4k.core.requirePositiveNumber
import kotlinx.atomicfu.atomic
import java.util.*

@Suppress("UNCHECKED_CAST")
class BoundedStack<E: Any> private constructor(val maxSize: Int): Stack<E>() {

    companion object {
        operator fun <E: Any> invoke(maxSize: Int): BoundedStack<E> {
            maxSize.requirePositiveNumber("maxSize")
            return BoundedStack(maxSize)
        }
    }

    val array: Array<E?> = arrayOfNulls<Any?>(maxSize) as Array<E?>

    private var top = 0

    private val counter = atomic(0)
    private var count by counter

    override val size: Int get() = count

    fun length(): Int = count

    override operator fun get(index: Int): E {
        if (index >= count) {
            throw IndexOutOfBoundsException(index.toString())
        }
        return array[(top + index) % maxSize] as E
    }

    @Synchronized
    override fun add(element: E): Boolean {
        push(element)
        return true
    }

    @Synchronized
    override fun addElement(obj: E) {
        throw UnsupportedOperationException()
    }

    @Synchronized
    override fun add(index: Int, element: E) {
        insert(index, element)
    }

    @Synchronized
    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { push(it) }
        return true
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    @Synchronized
    override fun push(item: E): E {
        top = if (top == 0) maxSize - 1 else top - 1
        array[top] = item
        if (count < maxSize) {
            counter.incrementAndGet()
        }
        return item
    }

    @Synchronized
    fun pushAll(vararg items: E) {
        items.forEach { push(it) }
    }

    @Synchronized
    override fun pop(): E {
        if (count == 0) {
            throw NoSuchElementException()
        }

        val item = array[top]
        top = (++top) % maxSize
        counter.decrementAndGet()
        return item!!
    }

    @Synchronized
    override fun peek(): E {
        if (count == 0) {
            throw NoSuchElementException()
        }
        return array[top]!!
    }

    @Synchronized
    fun insert(index: Int, elem: E): E {
        when {
            index == 0     -> return push(elem)
            index > count  -> throw java.lang.IndexOutOfBoundsException(index.toString())
            index == count -> {
                array[(top + index) % maxSize] = elem
                counter.incrementAndGet()
            }

            else           -> {
                val swapped = array[index]!!
                array[index] = elem
                insert(index - 1, swapped)
            }
        }
        return elem
    }

    @Synchronized
    fun update(index: Int, elem: E) {
        if (index > count)
            throw java.lang.IndexOutOfBoundsException(index.toString())
        array[(top + index) % maxSize] = elem
    }

    @Synchronized
    fun toList(): List<E> {
        return mutableListOf<E>().also { list -> forEach { list.add(it) } }
    }

    @Synchronized
    override fun iterator(): MutableIterator<E> {
        return object: MutableIterator<E> {

            private var index = 0
            override fun hasNext(): Boolean = index != count
            override fun next(): E = get(index++)
            override fun remove() {
                /* Nothing to do. */
            }
        }
    }
}
