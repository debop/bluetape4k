package io.bluetape4k.workshop.security.server.application.security.authentication

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.security.server.application.HttpExceptionFactory
import io.bluetape4k.workshop.security.server.application.security.JwtService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationSuccessHandler(
    private val jwtService: JwtService,
): ServerAuthenticationSuccessHandler {

    companion object: KLogging() {
        private const val FIFTEEN_MIN = 15 * 60 * 1000L
        private const val FOUR_HOURS = 4 * 60 * 60 * 1000L
    }

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange?,
        authentication: Authentication?,
    ): Mono<Void> = mono {
        val principal = authentication?.principal ?: throw HttpExceptionFactory.unauthorized()

        // 인증 성공 시에는 Authorization용 JWT와 Refresh용 JWT를 발급한다.
        when (principal) {
            is User -> {
                webFilterExchange?.exchange?.response?.headers?.let { headers ->
                    log.debug { "principal is User. generate access, refresh token... $principal" }
                    val roles = principal.authorities.map { it.authority }.toTypedArray()
                    val accessToken = jwtService.accessToken(principal.username, FIFTEEN_MIN, roles)
                    val refreshToken = jwtService.refreshToken(principal.username, FOUR_HOURS, roles)

                    headers.set(HttpHeaders.AUTHORIZATION, accessToken)
                    headers.set("JWT-Refresh-Token", refreshToken)
                }
            }
        }

        return@mono null
    }
}
