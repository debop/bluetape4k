package io.bluetape4k.bucket4j

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.ConfigurationBuilder

/**
 * Bucket configuration
 *
 * @param initializer [ConfigurationBuilder]를 이용한 초기화 람다
 * @return [BucketConfiguration] 인스턴스
 */
inline fun bucketConfiguration(initializer: ConfigurationBuilder.() -> Unit): BucketConfiguration =
    BucketConfiguration.builder().apply(initializer).build()


/**
 * 환경설정에 [Bandwidth]를 추가합니다.
 *
 * @param supplier [Bandwidth] 인스턴스를 생성하는 람다
 * @return [ConfigurationBuilder] 인스턴스
 */
inline fun ConfigurationBuilder.addBandwidth(supplier: () -> Bandwidth) = apply {
    addLimit(supplier())
}
