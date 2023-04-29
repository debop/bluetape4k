package io.bluetape4k.collections

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.logging.KLogging

/**
 * [Sequence]를 [batchSize] 크기로 나누어서 [Iterable] 요소를 제공하는 [Sequence]를 반환합니다.
 *
 * @property source 원본 [Sequence]
 * @property batchSize 요소 갯수 (기본값: 1)
 */
@Deprecated("use chunked() instead")
class BatchSequence<T> private constructor(
    val source: Sequence<T>,
    val batchSize: Int,
) : Sequence<Iterable<T>> {

    companion object : KLogging() {
        operator fun <T> invoke(source: Sequence<T>, batchSize: Int = 1): BatchSequence<T> {
            batchSize.assertPositiveNumber("batchSize")
            return BatchSequence(source, batchSize)
        }
    }

    override fun iterator(): Iterator<Iterable<T>> {
        return object : AbstractIterator<Iterable<T>>() {
            private val iter = source.iterator()

            override fun computeNext() {
                when {
                    iter.hasNext() -> setNext(iter.asSequence().take(batchSize).toList())
                    else -> done()
                }
            }
        }
    }
}

@Deprecated("use chunked() instead")
fun <T> Sequence<T>.batch(batchSize: Int = 1): Sequence<Iterable<T>> =
    BatchSequence(this, batchSize)


@Deprecated("use chunked() instead")
fun <T> Iterable<T>.batch(batchSize: Int = 1): Sequence<Iterable<T>> =
    BatchSequence(this.asSequence(), batchSize)
