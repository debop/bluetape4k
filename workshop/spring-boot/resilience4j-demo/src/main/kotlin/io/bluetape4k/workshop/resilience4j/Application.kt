package io.bluetape4k.workshop.resilience4j

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import java.net.URI

fun main(vararg args: String) {
    runApplication<Application>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}

@SpringBootApplication
class Application {

    companion object: KLogging()

    @Bean
    fun redirectRoot(): RouterFunction<ServerResponse> = coRouter {
        GET("/") {
            permanentRedirect(URI.create("/actuator")).build().awaitSingle()
        }
    }

    @Bean
    fun myRegistryEventConsumer(): RegistryEventConsumer<CircuitBreaker> {
        return object: RegistryEventConsumer<CircuitBreaker> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<CircuitBreaker>) {
                entryAddedEvent.addedEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry added. $event" }
                }
            }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<CircuitBreaker>) {
                entryRemoveEvent.removedEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry removed. $event" }
                }
            }

            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<CircuitBreaker>) {
                entryReplacedEvent.newEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry replaced. $event" }
                }
            }
        }
    }

    @Bean
    fun myRetryRegistryEventConsumer(): RegistryEventConsumer<Retry> {
        return object: RegistryEventConsumer<Retry> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<Retry>) {
                entryAddedEvent.addedEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry added. $event" }
                }
            }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<Retry>) {
                entryRemoveEvent.removedEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry removed. $event" }
                }
            }

            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<Retry>) {
                entryReplacedEvent.newEntry.eventPublisher.onEvent { event ->
                    log.info { "Entry replaced. $event" }
                }
            }
        }
    }
}
