package io.bluetape4k.infra.bucket4j

import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.ConfigurationBuilder

inline fun bucketConfiguration(initializer: ConfigurationBuilder.() -> Unit): BucketConfiguration =
    ConfigurationBuilder().apply(initializer).build()
