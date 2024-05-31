package io.wrtn.kommons.bloomfilter.inmemory

import io.wrtn.kommons.bloomfilter.BloomFilter
import io.wrtn.kommons.bloomfilter.DEFAULT_ERROR_RATE
import io.wrtn.kommons.bloomfilter.DEFAULT_MAX_NUM
import io.wrtn.kommons.bloomfilter.Hasher
import io.wrtn.kommons.bloomfilter.optimalK
import io.wrtn.kommons.bloomfilter.optimalM
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.info
import io.wrtn.kommons.support.assertPositiveNumber
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
