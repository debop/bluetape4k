package io.bluetape4k.workshop.es.controller

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import java.net.URI

@RestController
class Redirect {

    companion object: KLogging() {
        private const val SWAGGER_PATH = "/swagger-ui/index.html"
    }

    @GetMapping("/")
    suspend fun redirectToDocPage(): ServerResponse {
        return ServerResponse.permanentRedirect(URI(SWAGGER_PATH)).build().awaitSingle()
    }

    @GetMapping("/apidocs")
    suspend fun redirectToDocPage2(): ServerResponse {
        return ServerResponse.permanentRedirect(URI(SWAGGER_PATH)).build().awaitSingle()
    }
}
