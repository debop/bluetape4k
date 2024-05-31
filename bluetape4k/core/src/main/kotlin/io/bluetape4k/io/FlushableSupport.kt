package io.bluetape4k.io


import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.error
import java.io.Flushable
import java.io.IOException

private val logger by lazy { KotlinLogging.logger { } }

/**
 * Flush the [Flushable] quietly.
 */
fun Flushable.flushQuietly() {
    try {
        flush()
    } catch (e: IOException) {
        logger.error(e) { "Fail to flush." }
    }
}
