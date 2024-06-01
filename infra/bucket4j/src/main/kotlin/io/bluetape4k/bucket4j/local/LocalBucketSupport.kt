package io.bluetape4k.bucket4j.local

import io.github.bucket4j.Bucket
import io.github.bucket4j.local.LocalBucket
import io.github.bucket4j.local.LocalBucketBuilder

/**
 * [LocalBucket] instance 빌드를 위한 DSL 빌더
 *
 * @param initializer  [LocalBucketBuilder] 를 이용한 초기화 람다
 * @receiver
 * @return [LocalBucket] instance
 */
inline fun localBucket(initializer: LocalBucketBuilder.() -> Unit): LocalBucket =
    Bucket.builder().apply(initializer).build()

/**
 * Reconstructs a bucket from binary snapshot.
 *
 * @param bytes binary snapshot
 * @return [LocalBucket]
 */
fun localBucketOf(bytes: ByteArray): LocalBucket =
    LocalBucket.fromBinarySnapshot(bytes)

/**
 * Reconstructs a bucket from JSON snapshot.
 *
 * @param snapshot the snapshot Map that was deserialized from JSON via any JSON library
 *
 * @return bucket reconstructed from binary snapshot
 */
fun localBucketOf(snapshot: Map<String, Any?>): LocalBucket =
    LocalBucket.fromJsonCompatibleSnapshot(snapshot)
