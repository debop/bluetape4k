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
import org.slf4j.Logger

internal val logger by lazy { KotlinLogging.logger {} }

@Suppress("IMPLICIT_CAST_TO_ANY")
fun <T> Flow<T>.log(tag: Any, log: Logger = logger): Flow<T> =
    onStart { log.debug { "[$tag] Start " } }
        .onEach {
            val item = when (it) {
                is Flow<*> -> it.toList()
                else       -> it
            }
            log.debug { "[$tag] emit $item" }
        }
        .onCompletion {
            if (it == null) {
                log.debug { "[$tag] Completed" }
            } else {
                when (it) {
                    is CancellationException -> log.debug { "[$tag] Canceled" }
                    else                     -> log.debug(it) { "[$tag] Completed by exception" }
                }
            }
        }
        .onEmpty { log.debug { "[$tag] Flow is empty" } }
