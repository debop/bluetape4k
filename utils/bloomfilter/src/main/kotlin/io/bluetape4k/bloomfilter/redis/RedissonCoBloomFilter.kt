package io.bluetape4k.bloomfilter.redis

import io.bluetape4k.bloomfilter.CoBloomFilter
import io.bluetape4k.bloomfilter.DEFAULT_ERROR_RATE
import io.bluetape4k.bloomfilter.DEFAULT_MAX_NUM
import io.bluetape4k.bloomfilter.Hasher
import io.bluetape4k.bloomfilter.optimalK
import io.bluetape4k.bloomfilter.optimalM
import io.bluetape4k.coroutines.support.coAwait
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
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
