package io.bluetape4k.coroutines.context

import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend inline fun CoroutineContext.getOrCurrent(): CoroutineContext = when (this) {
    is EmptyCoroutineContext -> currentCoroutineContext()
    else                     -> this
}
