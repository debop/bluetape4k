package io.bluetape4k.spring.coroutines

import kotlinx.coroutines.future.await
import org.springframework.util.concurrent.ListenableFuture

suspend fun <T> ListenableFuture<T>.await(): T = completable().await()
