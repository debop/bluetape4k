@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

internal val logger = KotlinLogging.logger {}

fun <T> Flow<T>.log(tag: Any): Flow<T> =
    onStart { logger.debug { "[$tag] start " } }
        .onEach { logger.debug { "[$tag] emit $it" } }
        .onCompletion { logger.debug { "[$tag] complete $it" } }
