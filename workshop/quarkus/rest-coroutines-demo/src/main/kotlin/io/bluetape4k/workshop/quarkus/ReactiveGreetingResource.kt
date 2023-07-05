package io.bluetape4k.workshop.quarkus

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.quarkus.config.GreetingConfig
import io.bluetape4k.workshop.quarkus.model.Greeting
import io.bluetape4k.workshop.quarkus.services.ReactiveGreetingService
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.jboss.resteasy.reactive.RestStreamElementType
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * Quarkus Reactive with Kotlin
 *
 * 참고: [Quarkus - Using Kotlin](https://quarkus.io/guides/kotlin)
 */
@Path("/reactive")
@Produces(MediaType.APPLICATION_JSON)
class ReactiveGreetingResource(private val greetingService: ReactiveGreetingService) {

    companion object: KLogging()

    /** Configuration 정보를 injection 받습니다 */
    @Inject
    internal lateinit var greetingConfig: GreetingConfig

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Blocking                           // Execution을 Worker Thread에서 수행하다는 것을 명시합니다.
    fun hello(): String {
        log.debug { "call blocking method" }
        Thread.sleep(100)
        return "${greetingConfig.message} ${greetingConfig.name}${greetingConfig.suffix}"
    }

    @GET
    @Path("/greeting/{name}")
    fun greeting(name: String): Uni<Greeting> {
        return greetingService.greeting(name)
    }

    @GET
    @Path("/greeting/{count}/{name}")
    fun greetings(count: Int, name: String): Multi<Greeting> {
        return greetingService.greetings(count, name)
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/stream/{count}/{name}")
    fun greetingAsStream(count: Int, name: String): Multi<Greeting> {
        return greetingService.greetings(count, name)
    }
}
