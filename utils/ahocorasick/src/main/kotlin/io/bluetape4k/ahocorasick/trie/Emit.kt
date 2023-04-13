package io.bluetape4k.ahocorasick.trie

import io.bluetape4k.ahocorasick.interval.Interval

class Emit(
    override val start: Int,
    override val end: Int,
    val keyword: String? = null,
) : Interval(start, end) {

    override fun toString(): String = super.toString() + "=${keyword ?: "<null>"}"
}
