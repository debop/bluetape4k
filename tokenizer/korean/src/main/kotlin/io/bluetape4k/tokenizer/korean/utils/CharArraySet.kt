package io.bluetape4k.tokenizer.korean.utils

import java.io.Serializable

/**
 * Lucuene 에 있는 CharArraySet 을 Kotlin 으로 porting 한 클래스입니다
 * 사전 정보를 관리하는 클래스입니다.
 */
open class CharArraySet(val map: CharArrayMap<Any>): java.util.AbstractSet<Any>(), Serializable {

    companion object {
        private val EMPTY_SET = CharArraySet(CharArrayMap.emptyMap())
        private val PLACEHOLDER = Any()

        @Suppress("USELESS_IS_CHECK")
        fun unmodifiableSet(set: CharArraySet): CharArraySet {
            return when (set) {
                EMPTY_SET                                   -> EMPTY_SET
                is CharArrayMap.UnmodifiableCharArrayMap<*> -> set
                else                                        -> CharArraySet(CharArrayMap.unmodifiableMap(set.map))
            }
        }

        fun copy(set: Set<Any>): CharArraySet = when (set) {
            EMPTY_SET       -> EMPTY_SET
            is CharArraySet -> CharArraySet(CharArrayMap.copy(set.map))
            else            -> CharArraySet(set)
        }
    }

    constructor(startSize: Int): this(CharArrayMap<Any>(startSize))

    constructor(c: Collection<Any>): this(c.size) {
        @Suppress("LeakingThis")
        addAll(c)
    }

    override fun clear() {
        map.clear()
    }

    override fun contains(element: Any?): Boolean = map.containsKey(element)
    fun contains(text: CharArray, off: Int, len: Int) = map.containsKey(text, off, len)
    fun contains(cs: CharSequence) = map.containsKey(cs)

    override fun add(element: Any): Boolean = map.put(element, PLACEHOLDER) == null
    open fun add(text: CharSequence) = map.put(text, PLACEHOLDER) == null
    open fun add(text: String) = map.put(text, PLACEHOLDER) == null
    open fun add(text: CharArray) = map.put(text, PLACEHOLDER) == null

    override fun addAll(elements: Collection<Any>): Boolean {
        var modified = false
        elements.forEach {
            modified = add(it)
        }
        return modified
    }

    override fun remove(element: Any?): Boolean {
        return element?.let {
            map.remove(it)
            map.containsKey(it)
        } ?: false
    }

    override fun removeAll(elements: Collection<Any>): Boolean {
        var removed = false
        elements.forEach {
            removed = removed || remove(it)
        }
        return removed
    }

    override val size: Int
        get() = map.size

    override fun iterator(): MutableIterator<Any> {
        return map.originalKeySet.iterator()
    }

    override fun toString(): String {
        val sb = StringBuilder("[")
        for (item in this) {
            if (sb.length > 1) sb.append(", ")
            when (item) {
                is CharArray -> sb.append(item)
                else         -> sb.append(item.toString())
            }
        }
        return sb.append("]").toString()
    }
}
