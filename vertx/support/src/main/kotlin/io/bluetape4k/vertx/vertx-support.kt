package io.bluetape4k.vertx

import io.vertx.core.Vertx

/**
 * Get current [Vertx] instance.
 */
fun currentVertx(): Vertx = Vertx.currentContext()?.owner() ?: Vertx.vertx()
