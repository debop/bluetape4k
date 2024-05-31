package io.wrtn.kommons.bloomfilter.redis

import io.wrtn.kommons.bloomfilter.CoBloomFilter
import io.wrtn.kommons.bloomfilter.DEFAULT_ERROR_RATE
import io.wrtn.kommons.bloomfilter.DEFAULT_MAX_NUM
import io.wrtn.kommons.bloomfilter.Hasher
import io.wrtn.kommons.bloomfilter.optimalK
import io.wrtn.kommons.bloomfilter.optimalM
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.info
import io.wrtn.kommons.redis.redisson.coroutines.coAwait
import org.redisson.api.RedissonClient

class RedissonCoBloomFilter<T: Any> private constructor(
    private val redisson: RedissonClient,
    private val bloomName: String,
    override val m: Int,
    override val k: Int,
): CoBloomFilter<T> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T: Any> invoke(
            redisson: RedissonClient,
            bloomName: String,
            maxNum: Long = DEFAULT_MAX_NUM,
            errorRate: Double = DEFAULT_ERROR_RATE,
        ): RedissonCoBloomFilter<T> {
            val m = optimalM(maxNum, errorRate)
            val k = optimalK(maxNum, m)

            return RedissonCoBloomFilter<T>(redisson, bloomName, m, k).apply {
                log.info { "Create RedissonBloomFilter, name=$bloomName, m=$m, k=$k" }
            }
        }
    }

    override val isEmpty: Boolean get() = !redisson.getBitSet(bloomName).isExists

    override suspend fun add(value: T) {
        val offsets = Hasher.murmurHashOffset(value, k, m)

        val batch = redisson.createBatch()
        val bloomAsync = batch.getBitSet(bloomName)

        offsets.forEach { bloomAsync.setAsync(it.toLong()) }
        batch.executeAsync().coAwait()
    }

    override suspend fun contains(value: T): Boolean {
        val offsets = Hasher.murmurHashOffset(value, k, m)
        val batch = redisson.createBatch()
        val bloomAsync = batch.getBitSet(bloomName)

        offsets.forEach { bloomAsync.getAsync(it.toLong()) }
        val result = batch.executeAsync().coAwait()

        return result.responses.all { it as Boolean }
    }

    override fun count(): Long {
        return redisson.getBitSet(bloomName).length()
    }

    override suspend fun clear() {
        redisson.getBitSet(bloomName).clearAsync().coAwait()
    }
}
