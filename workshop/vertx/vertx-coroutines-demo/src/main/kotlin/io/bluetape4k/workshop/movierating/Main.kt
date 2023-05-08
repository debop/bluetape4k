package io.bluetape4k.workshop.movierating

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await

suspend fun main() {
    val vertx = Vertx.vertx()

    try {
        val result = vertx.deployVerticle(MovieRatingVerticle()).await()
        println("Application started. $result")
    } catch (e: Throwable) {
        println("Could not start application.")
        e.printStackTrace()
    }
}
