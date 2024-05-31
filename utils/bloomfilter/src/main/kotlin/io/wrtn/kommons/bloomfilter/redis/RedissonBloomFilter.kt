package io.wrtn.kommons.bloomfilter.redis

import io.wrtn.kommons.bloomfilter.BloomFilter
import io.wrtn.kommons.bloomfilter.DEFAULT_ERROR_RATE
import io.wrtn.kommons.bloomfilter.DEFAULT_MAX_NUM
import io.wrtn.kommons.bloomfilter.Hasher
import io.wrtn.kommons.bloomfilter.optimalK
import io.wrtn.kommons.bloomfilter.optimalM
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.info
import org.redisson.api.RedissonClient

class RedissonBloomFilter<T: Any> private constructor(
    private val redisson: RedissonClient,
    private val bloomName: String,
    override val m: Int,
    override val k: Int,
): BloomFilter<T> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T: Any> invoke(
            redisson: RedissonClient,
            bloomName: String,
            maxNum: Long = DEFAULT_MAX_NUM,
            errorRate: Double = DEFAULT_ERROR_RATE,
        ): RedissonBloomFilter<T> {
            val m = optimalM(maxNum, errorRate)
            val k = optimalK(maxNum, m)

            return RedissonBloomFilter<T>(redisson, bloomName, m, k).apply {
                log.info { "Create RedissonBloomFilter, name=$bloomName, m=$m, k=$k" }
            }
        }
    }

    override val isEmpty: Boolean get() = !redisson.getBitSet(bloomName).isExists

    override fun add(value: T) {
        val offsets = Hasher.murmurHashOffset(value, k, m)

        val batch = redisson.createBatch()
        val bloomAsync = batch.getBitSet(bloomName)

        offsets.forEach { bloomAsync.setAsync(it.toLong()) }
        batch.execute()
    }

    override fun contains(value: T): Boolean {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        val batch = redisson.createBatch()
        val bloomAsync = batch.getBitSet(bloomName)

        offsets.forEach { bloomAsync.getAsync(it.toLong()) }
        val result = batch.execute()

        return result.responses.all { it as Boolean }
    }

    override fun count(): Long {
        return redisson.getBitSet(bloomName).length()
    }

    override fun clear() {
        redisson.getBitSet(bloomName).clear()
    }
}
