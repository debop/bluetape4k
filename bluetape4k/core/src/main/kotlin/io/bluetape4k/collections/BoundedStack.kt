package io.bluetape4k.collections

import io.bluetape4k.support.requirePositiveNumber
import kotlinx.atomicfu.atomic
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 저장 크기가 [maxSize]로 제한된 Stack 입니다.
 */
@Suppress("UNCHECKED_CAST")
class BoundedStack<E: Any> private constructor(val maxSize: Int): Stack<E>() {

    companion object {
        operator fun <E: Any> invoke(maxSize: Int): BoundedStack<E> {
            maxSize.requirePositiveNumber("maxSize")
            return BoundedStack(maxSize)
        }
    }

    val array: Array<E?> = arrayOfNulls<Any?>(maxSize) as Array<E?>

    private val counter = atomic(0)

    @Volatile
    private var top = 0
    private val lock = ReentrantLock()

    override val size: Int get() = counter.value

    fun length(): Int = counter.value

    override operator fun get(index: Int): E {
        if (index >= counter.value) {
            throw IndexOutOfBoundsException(index.toString())
        }
        return array[(top + index) % maxSize] as E
    }

    override fun add(element: E): Boolean {
        return lock.withLock {
            push(element)
            true
        }
    }

    override fun addElement(obj: E) {
        throw UnsupportedOperationException()
    }

    override fun add(index: Int, element: E) {
        return lock.withLock {
            insert(index, element)
        }
    }

    override fun addAll(elements: Collection<E>): Boolean {
        lock.withLock {
            elements.forEach { push(it) }
            return true
        }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun push(item: E): E {
        lock.withLock {
            top = if (top == 0) maxSize - 1 else top - 1
            array[top] = item
            if (counter.value < maxSize) {
                counter.incrementAndGet()
            }
            return item
        }
    }

    fun pushAll(vararg items: E) {
        lock.withLock {
            items.forEach { push(it) }
        }
    }

    override fun pop(): E {
        lock.withLock {
            if (counter.value == 0) {
                throw NoSuchElementException()
            }

            val item = array[top]
            top = (++top) % maxSize
            counter.decrementAndGet()
            return item!!
        }
    }

    override fun peek(): E {
        lock.withLock {
            if (counter.value == 0) {
                throw NoSuchElementException()
            }
            return array[top]!!
        }
    }

    fun insert(index: Int, elem: E): E {
        return lock.withLock {
            when {
                index == 0             -> return push(elem)
                index > counter.value  -> throw java.lang.IndexOutOfBoundsException(index.toString())
                index == counter.value -> {
                    array[(top + index) % maxSize] = elem
                    counter.incrementAndGet()
                }

                else                   -> {
                    val swapped = array[index]!!
                    array[index] = elem
                    insert(index - 1, swapped)
                }
            }
            elem
        }
    }

    fun update(index: Int, elem: E) {
        lock.withLock {
            if (index > counter.value) {
                throw IndexOutOfBoundsException(index.toString())
            }
            array[(top + index) % maxSize] = elem
        }
    }

    fun toList(): MutableList<E> {
        return lock.withLock {
            val results = mutableListOf<E>()
            forEach {
                results.add(it)
            }
            results
        }
    }

    override fun iterator(): MutableIterator<E> {
        return lock.withLock {
            toList().iterator()
        }
    }
}
