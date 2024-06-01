package io.bluetape4k.bucket4j.coroutines

import io.bluetape4k.logging.KLogging
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.ConfigurationBuilder
import io.github.bucket4j.TimeMeter

/**
 * Suspending bucket configuration
 *
 * @property timeMeter  Set's the TimeMeter to be used by the underlying Bucket4j objects.
 *                      By default, you may choose either TimeMeter.SYSTEM_MILLISECONDS or TimeMeter.SYSTEM_NANOSECONDS.
 *                      Caution: see Bucket4j's [documentation about using nanoseconds](https://bucket4j.com/8.2.0/toc.html#customizing-time-measurement-choosing-nanotime-time-resolution)
 *                      before choosing that option.
 * @property mutableLimits
 * @constructor Create empty Suspending bucket configuration
 */
@Deprecated("use bucketConfiguration method instead")
data class SuspendingBucketConfiguration(
    val timeMeter: TimeMeter = TimeMeter.SYSTEM_MILLISECONDS,
    val bandwidths: MutableList<Bandwidth> = mutableListOf(),
) {
    companion object: KLogging()

    /**
     * Adds a [bandwidth] limit to the bucket.
     * See the Bucket4j documentation on [Bandwidths](https://bucket4j.com/8.2.0/toc.html#bandwidth) for more information.
     *
     * @param bandwidth limits [bandwidth]
     */
    fun addLimit(bandwidth: Bandwidth) {
        bandwidths.add(bandwidth)
    }

    fun addLimit(bandwidthSupplier: () -> Bandwidth) {
        bandwidths.add(bandwidthSupplier())
    }

    fun toBucketConfiguration(): BucketConfiguration {
        return ConfigurationBuilder()
            .also { builder ->
                bandwidths.forEach { bandwidth ->
                    builder.addLimit(bandwidth)
                }
            }
            .build()
    }
}
