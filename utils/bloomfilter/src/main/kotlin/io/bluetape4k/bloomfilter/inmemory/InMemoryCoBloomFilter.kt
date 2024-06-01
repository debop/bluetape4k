package io.bluetape4k.bloomfilter.inmemory

import io.bluetape4k.bloomfilter.CoBloomFilter
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
