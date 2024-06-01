package io.bluetape4k.bloomfilter.inmemory

import io.bluetape4k.bloomfilter.BloomFilter
import io.bluetape4k.bloomfilter.DEFAULT_ERROR_RATE
import io.bluetape4k.bloomfilter.DEFAULT_MAX_NUM
import io.bluetape4k.bloomfilter.Hasher
import io.bluetape4k.bloomfilter.optimalK
import io.bluetape4k.bloomfilter.optimalM
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.assertPositiveNumber
import java.io.Serializable
import java.util.*

open class InMemoryBloomFilter<T: Serializable> private constructor(
    override val m: Int,
    override val k: Int,
): BloomFilter<T> {

    companion object: KLogging() {

        protected const val SEED32: Int = 89478583

        @JvmStatic
        operator fun <T: Serializable> invoke(
            maxNum: Long = DEFAULT_MAX_NUM,
            errorRate: Double = DEFAULT_ERROR_RATE,
        ): InMemoryBloomFilter<T> {
            val m = optimalM(maxNum, errorRate).assertPositiveNumber("m")
            val k = optimalK(maxNum, m).assertPositiveNumber("k")

            return InMemoryBloomFilter<T>(m, k).apply {
                log.info { "Create InMemoryBloomFilter. m=$m, k=$k" }
            }
        }
    }

    protected val bloom: BitSet = BitSet(m)

    override val isEmpty: Boolean get() = bloom.isEmpty

    override fun count(): Long = m.toLong()

    override fun add(value: T) {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        offsets.forEach { setBit(it, true) }
    }

    override fun contains(value: T): Boolean {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        return offsets.all { getBit(it) }
    }

    override fun clear() {
        bloom.clear()
    }

    protected fun getBit(index: Int): Boolean = bloom[index]

    protected fun setBit(index: Int, value: Boolean = true) {
        bloom[index] = value
    }

}
