package io.bluetape4k.quarkus.kotlin.resteasy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren

/**
 * REST Resource 를 Coroutines 환경에서 실행하도록 [CoroutineScope] 구현체를 제공합니다.
 * ```
 * @Path("/fruits")
 * class FruitResource: AbstractCoroutineResource {
 *      // ...
 * }
 * ```
 */
abstract class AbstractCoroutineResource: CoroutineScope, AutoCloseable {

    override val coroutineContext = SupervisorJob() + Dispatchers.IO

    override fun close() {
        runCatching { coroutineContext.cancelChildren() }
        runCatching { coroutineContext.cancel() }
    }
}
