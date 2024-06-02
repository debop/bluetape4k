package io.bluetape4k.spring.coroutines

import kotlinx.coroutines.future.await
import org.springframework.util.concurrent.ListenableFuture

@Suppress("DEPRECATION")
@Deprecated("use kotlinx.coroutines await")
suspend inline fun <T> ListenableFuture<T>.await(): T = completable().await()
