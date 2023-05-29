package io.bluetape4k.collections.eclipse.ranges

import io.bluetape4k.collections.size
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList

fun CharProgression.toCharArrayList(): CharArrayList {
    val array = CharArrayList(this.count())
    this.forEach { array.add(it) }
    return array
}

fun IntProgression.toIntArrayList(): IntArrayList =
    IntArrayList(this.size()).also { list ->
        forEach { list.add(it) }
    }

fun LongProgression.toLongArrayList(): LongArrayList {
    return LongArrayList(this.size())
        .also { list ->
            forEach { list.add(it) }
        }
}
