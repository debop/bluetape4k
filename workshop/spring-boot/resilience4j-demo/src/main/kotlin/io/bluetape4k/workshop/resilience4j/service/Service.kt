package io.bluetape4k.workshop.resilience4j.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

interface Service {

    fun failure(): String

    fun failureWithFallback(): String

    fun success(): String

    fun successException(): String

    fun ignoreException(): String

    fun fluxSuccess(): Flux<String>

    fun fluxFailure(): Flux<String>

    fun fluxTimeout(): Flux<String>

    fun monoSuccess(): Mono<String>

    fun monoFailure(): Mono<String>

    fun monoTimeout(): Mono<String>

    fun futureSuccess(): CompletableFuture<String>

    fun futureFailure(): CompletableFuture<String>

    fun futureTimeout(): CompletableFuture<String>
}
