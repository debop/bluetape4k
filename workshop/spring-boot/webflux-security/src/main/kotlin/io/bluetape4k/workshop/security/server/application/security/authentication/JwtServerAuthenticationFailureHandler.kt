package io.bluetape4k.workshop.security.server.application.security.authentication

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.security.server.application.HttpExceptionFactory
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler {

    companion object: KLogging()

    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange?,
        exception: AuthenticationException?,
    ): Mono<Void> = mono {
        val exchange = webFilterExchange?.exchange ?: throw HttpExceptionFactory.unauthorized()

        log.debug { "Authentication failed. ${exception?.message}" }

        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        exchange.response.setComplete().awaitFirstOrNull()
    }
}
