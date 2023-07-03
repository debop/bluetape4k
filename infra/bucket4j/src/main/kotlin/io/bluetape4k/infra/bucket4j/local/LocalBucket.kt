package io.bluetape4k.infra.bucket4j.local

import io.github.bucket4j.Bucket
import io.github.bucket4j.local.LocalBucket
import io.github.bucket4j.local.LocalBucketBuilder

inline fun localBucket(initializer: LocalBucketBuilder.() -> Unit): LocalBucket =
    Bucket.builder().apply(initializer).build()
