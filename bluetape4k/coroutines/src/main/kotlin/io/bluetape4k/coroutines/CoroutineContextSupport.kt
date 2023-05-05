package io.bluetape4k.coroutines

import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun CoroutineContext.getOrCurrent(): CoroutineContext =
    if (this == EmptyCoroutineContext) currentCoroutineContext() else this
