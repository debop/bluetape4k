package io.bluetape4k.workshop.quarkus

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.quarkus.kotlin.resteasy.AbstractCoroutineResource
import io.bluetape4k.workshop.quarkus.client.CoroutineGreetingClient
import io.bluetape4k.workshop.quarkus.config.GreetingConfig
import io.bluetape4k.workshop.quarkus.model.Greeting
import io.bluetape4k.workshop.quarkus.services.CoroutineGreetingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import org.eclipse.microprofile.rest.client.RestClientBuilder
import org.jboss.resteasy.reactive.RestStreamElementType
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@Path("/coroutines")
@Produces(MediaType.APPLICATION_JSON)
class CoroutineGreetingResource(
    private val greetingService: CoroutineGreetingService,
): AbstractCoroutineResource() {

    companion object: KLogging()

    /** Configuration 정보를 injection 받습니다 */
    @Inject
    internal lateinit var greetingConfig: GreetingConfig

    /**
     * Current Resource에 대한 [UriInfo] 정보
     */
    @Context
    internal lateinit var uriInfo: UriInfo

    /**
     * `@RestClient` 를 적용하여 Inject 받을 수 있습니다.
     * baseUri 등의 정보는 application.properties 에서 읽어오게 하면 됩니다. (참고: rest-client-demo)
     *
     * NOTE: 실제 사용 시에는 `@RestClient` 로 injection 받는 것이 낫다 (baseUri 를 얻기 위해서 어쩔 수 없이 RestClientBuilder를 사용하였다)
     */
    private val greetingClient: CoroutineGreetingClient by lazy {
        log.info { "Create CoroutineGreetingClient. baseUri=${uriInfo.baseUri}" }
        RestClientBuilder.newBuilder()
            .baseUri(uriInfo.baseUri)    // or quarkus.test-host.url 을 써도 된다.
            .build(CoroutineGreetingClient::class.java)
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    suspend fun hello(): String {
        log.debug { "call suspend method" }
        delay(10)
        return "${greetingConfig.message} ${greetingConfig.name}${greetingConfig.suffix}"
    }

    @Path("/sequential-hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun helloSequential(): Flow<String> = flow {
        repeat(4) {
            emit(getRemoteHello())
        }
    }.onEach {
        log.debug { "sequential emit: $it" }
    }

    @GET
    @Path("/greeting/{name}")
    suspend fun greeting(name: String): Greeting {
        return greetingService.greeting(name)
    }

    @GET
    @Path("/sequential-greeting/{name}")
    fun sequentialGreeting(name: String): Flow<Greeting> = flow {
        repeat(4) {
            emit(getGreeting(name))
        }
    }.onEach {
        log.debug { "sequential emit: $it" }
    }

    @GET
    @Path("/greeting/{count}/{name}")
    fun greetings(count: Int, name: String): Flow<Greeting> {
        return greetingService.greetings(count, name)
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/stream/{count}/{name}")
    fun greetingAsStream(count: Int, name: String): Flow<Greeting> {
        return greetingService.greetings(count, name)
    }

    /**
     * [CoroutineGreetingClient] 를 이용하여 [CoroutineGreetingResource] 를 호출합니다
     * 외부 REST API 를 호출할 때도 [RestClientBuilder] 를 이용하여 Retroft2 처럼 사용할 수 있습니다.
     */
    private suspend fun getRemoteHello(): String {
        log.debug { "Get hello via remote client" }
        return greetingClient.hello()
    }

    private suspend fun getGreeting(name: String): Greeting {
        log.debug { "Get remote greeting. name=$name" }
        return greetingClient.greeting(name)
    }
}
