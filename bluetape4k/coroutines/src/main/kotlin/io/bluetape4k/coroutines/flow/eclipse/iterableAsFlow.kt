@file:JvmMultifileClass
@file:JvmName("FlowEclipseCollectionKt")

package io.bluetape4k.coroutines.flow.eclipse

import io.bluetape4k.collections.eclipse.primitives.asSequence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.eclipse.collections.api.CharIterable
import org.eclipse.collections.api.DoubleIterable
import org.eclipse.collections.api.FloatIterable
import org.eclipse.collections.api.IntIterable
import org.eclipse.collections.api.LongIterable
import org.eclipse.collections.api.RichIterable
import org.eclipse.collections.api.ShortIterable

fun <T> RichIterable<T>.asFlow(): Flow<T> = asSequence().asFlow()

fun CharIterable.asFlow(): Flow<Char> = asSequence().asFlow()
fun ShortIterable.asFlow(): Flow<Short> = asSequence().asFlow()
fun IntIterable.asFlow(): Flow<Int> = asSequence().asFlow()
fun LongIterable.asFlow(): Flow<Long> = asSequence().asFlow()
fun FloatIterable.asFlow(): Flow<Float> = asSequence().asFlow()
fun DoubleIterable.asFlow(): Flow<Double> = asSequence().asFlow()
