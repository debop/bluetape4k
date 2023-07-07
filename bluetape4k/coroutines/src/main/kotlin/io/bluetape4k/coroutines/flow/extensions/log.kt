@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList

internal val logger = KotlinLogging.logger {}

@Suppress("IMPLICIT_CAST_TO_ANY")
fun <T> Flow<T>.log(tag: Any): Flow<T> =
    onStart { logger.debug { "[$tag] Start " } }
        .onEach {
            val item = when (it) {
                is Flow<*> -> it.toList()
                else       -> it
            }
            logger.debug { "[$tag] emit $item" }
        }
        .onCompletion {
            if (it == null) {
                logger.debug { "[$tag] Completed" }
            } else {
                when (it) {
                    is CancellationException -> logger.debug { "[$tag] Canceled" }
                    else                     -> logger.debug(it) { "[$tag] Completed by exception" }
                }
            }
        }
        .onEmpty { logger.debug { "[$tag] Flow is empty" } }
