package io.wrtn.kommons.bloomfilter.inmemory

import io.wrtn.kommons.bloomfilter.CoBloomFilter
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

class InMemoryCoBloomFilter<T: Any>(
    override val m: Int,
    override val k: Int,
): CoBloomFilter<T> {

    companion object: KLogging() {
        protected const val SEED32: Int = 89478583

        @JvmStatic
        operator fun <T: Serializable> invoke(
            maxNum: Long = DEFAULT_MAX_NUM,
            errorRate: Double = DEFAULT_ERROR_RATE,
        ): InMemoryCoBloomFilter<T> {
            val m = optimalM(maxNum, errorRate).assertPositiveNumber("m")
            val k = optimalK(maxNum, m).assertPositiveNumber("k")

            return InMemoryCoBloomFilter<T>(m, k).apply {
                log.info { "Create InMemoryBloomFilter. m=$m, k=$k" }
            }
        }
    }

    private val bloom: BitSet = BitSet(m)

    override val isEmpty: Boolean
        get() = bloom.isEmpty

    override fun count(): Long = m.toLong()

    override suspend fun clear() {
        bloom.clear()
    }

    override suspend fun contains(value: T): Boolean {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        return offsets.all { bloom[it] }
    }

    override suspend fun add(value: T) {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        offsets.forEach { bloom[it] = true }
    }
}
