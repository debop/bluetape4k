package io.bluetape4k.bloomfilter.inmemory

import io.bluetape4k.bloomfilter.DEFAULT_ERROR_RATE
import io.bluetape4k.bloomfilter.DEFAULT_MAX_NUM
import io.bluetape4k.bloomfilter.Hasher
import io.bluetape4k.bloomfilter.MutableBloomFilter
import io.bluetape4k.bloomfilter.optimalK
import io.bluetape4k.bloomfilter.optimalM
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.assertPositiveNumber
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.support.setAll
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class InMemoryMutableBloomFilter private constructor(
    override val m: Int,
    override val k: Int,
): MutableBloomFilter<String> {

    companion object: KLogging() {
        private const val SEED: Int = 89478583
        private const val HASH_LOCK_SIZE = 16
        private const val BUCKET_MAX_VALUE = HASH_LOCK_SIZE - 1

        @JvmStatic
        operator fun invoke(
            maxNum: Long = DEFAULT_MAX_NUM,
            errorRate: Double = DEFAULT_ERROR_RATE,
        ): InMemoryMutableBloomFilter {
            val m = optimalM(maxNum, errorRate).assertPositiveNumber("m")
            val k = optimalK(maxNum, m).assertPositiveNumber("k")

            return InMemoryMutableBloomFilter(m, k)
        }

        private fun buckets2words(m: Int): Int = ((m - 1) ushr 4) + 1

        private fun long2bytes(num: Long): ByteArray =
            ByteArray(8) { (num ushr (56 - (it * 8))).toByte() }
    }

    private val buckets: LongArray = LongArray(buckets2words(m))
    private val hashLocks: Array<ReentrantLock> = Array(HASH_LOCK_SIZE) { ReentrantLock() }
    private val hashBooleans: Array<AtomicBoolean> = Array(HASH_LOCK_SIZE) { atomic(false) }
    private val lock = ReentrantLock()

    override val isEmpty: Boolean
        get() = buckets.all { it == 0L }

    override fun count(): Long = m.toLong()

    override fun add(value: String) {
        value.requireNotBlank("value")

        val hashes = Hasher.murmurHashOffset(value, k, m)
        var reuse = false

        repeat(k) { i ->
            val hash = hashes[i]
            val (wordNum, bucketShift, bucketMask) = calcBucketInfo(hash)
            var isExecuted = false

            while (!isExecuted && (reuse || (!reuse && getHashBoolean(hash).compareAndSet(false, true)))) {
                val bucketValue = (buckets[wordNum] and bucketMask) ushr bucketShift
                if (bucketValue < BUCKET_MAX_VALUE) {
                    buckets[wordNum] = (buckets[wordNum] and bucketMask.inv()) or ((bucketValue + 1) shl bucketShift)
                }

                if (i + 1 >= k || hashes[i + 1] != hashes[i]) {
                    reuse = false
                    getHashBoolean(hash).compareAndSet(true, false)
                } else {
                    reuse = true
                }
                isExecuted = true
            }
        }
    }

    override fun contains(value: String): Boolean {
        value.requireNotBlank("value")
        val hashes = Hasher.murmurHashOffset(value, k, m)

        repeat(k) { i ->
            val hash = hashes[i]
            val (wordNum, _, bucketMask) = calcBucketInfo(hash)
            if ((buckets[wordNum] and bucketMask) == 0L) {
                return false
            }
        }
        return true
    }

    override fun remove(value: String) {
        value.requireNotBlank("value")
        if (!contains(value)) {
            return
        }

        val hashes = Hasher.murmurHashOffset(value, k, m)
        var reuse = false

        repeat(k) { i ->
            val hash = hashes[i]
            val (wordNum, bucketShift, bucketMask) = calcBucketInfo(hash)
            var isExecuted = false

            while (!isExecuted && (reuse || (!reuse && getHashBoolean(hash).compareAndSet(false, true)))) {
                val bucketValue = (buckets[wordNum] and bucketMask) ushr bucketShift
                if (bucketValue in 1 until BUCKET_MAX_VALUE) {
                    buckets[wordNum] = (buckets[wordNum] and bucketMask.inv()) or ((bucketValue - 1) shl bucketShift)
                    hashBooleans[BUCKET_MAX_VALUE].compareAndSet(true, false)
                }

                if (i + 1 >= k || hashes[i + 1] != hashes[i]) {
                    reuse = false
                    getHashBoolean(hash).compareAndSet(true, false)
                } else {
                    reuse = true
                }
                isExecuted = true
            }
        }
    }

    /**
     * 하나의 key가 얼마나 추가되었는지 추정한다
     *
     * @param value
     * @return
     */
    override fun approximateCount(value: String): Int {
        value.requireNotBlank("value")

        var res = Int.MAX_VALUE
        val hashes = Hasher.murmurHashOffset(value, k, m)

        repeat(k) { i ->
            val hash = hashes[i]
            val (wordNum, bucketShift, bucketMask) = calcBucketInfo(hash)
            val bucketValue = (buckets[wordNum] and bucketMask) ushr bucketShift

            if (bucketValue < res.toLong())
                res = bucketValue.toInt()
        }
        return if (res != Int.MAX_VALUE) res else 0
    }


    override fun clear() {
        lock.withLock {
            buckets.setAll { 0L }
            hashLocks.filter { it.isLocked }.forEach { it.unlock() }
            hashBooleans.forEach { it.value = false }
        }
    }

    override fun toString(): String = buildString {
        repeat(m) { i ->
            if (i > 0) {
                append(" ")
            }
            val (wordNum, bucketShift, bucketMask) = calcBucketInfo(i)
            val bucketValue = (buckets[wordNum] ushr bucketShift) and bucketMask
            append(bucketValue)
        }
    }

    private fun getHashBoolean(hash: Int): AtomicBoolean {
        return hashBooleans[hash % hashBooleans.size]
    }

    private fun calcBucketInfo(hash: Int): Triple<Int, Int, Long> {
        val wordNum = hash ushr 4
        val bucketShift = (hash and 0x0F) shl 2  // hash.rem(16) shl 2
        val bucketMask = BUCKET_MAX_VALUE.toLong() shl bucketShift

        return Triple(wordNum, bucketShift, bucketMask)
    }
}
