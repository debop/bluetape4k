package io.bluetape4k.coroutines.flow

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.primitives.lastIndex
import io.bluetape4k.collections.eclipse.unifiedSetOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

suspend fun <T> Flow<T>.toFastList(destination: FastList<T> = fastListOf()): FastList<T> {
    collect { value -> destination.add(value) }
    return destination
}

suspend fun <T> Flow<T>.toUnifiedSet(destination: UnifiedSet<T> = unifiedSetOf()): UnifiedSet<T> {
    collect { value -> destination.add(value) }
    return destination
}

suspend fun IntArrayList.asFlow(): Flow<Int> = flow {
    for (i in 0..lastIndex) {
        emit(get(i))
    }
}

suspend fun LongArrayList.asFlow(): Flow<Long> = flow {
    for (i in 0..lastIndex) {
        emit(get(i))
    }
}

suspend fun FloatArrayList.asFlow(): Flow<Float> = flow {
    for (i in 0..lastIndex) {
        emit(get(i))
    }
}

suspend fun DoubleArrayList.asFlow(): Flow<Double> = flow {
    for (i in 0..lastIndex) {
        emit(get(i))
    }
}
