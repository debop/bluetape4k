package io.bluetape4k.workshop.security.server.application.security.authorization

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.warn
import io.bluetape4k.workshop.security.server.application.security.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtReactiveAuthorizationFilter(private val jwtService: JwtService): WebFilter {

    companion object: KLogging()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // Header 의 "Authorization: Bearer {token}" 을 가져온다.
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: run {
                log.debug { "Authorization header is not found" }
                return chain.filter(exchange)
            }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn { "Authorization header does not start with Bearer" }
            return chain.filter(exchange)
        }

        // JWT 토큰을 디코딩하고, reactive context 에 인증 정보를 저장한다.
        return try {
            val token = jwtService.decodeAccessToken(authHeader)
            log.debug { "Token subject=${token.subject}, roles=${jwtService.getRoles(token)}" }

            val auth = UsernamePasswordAuthenticationToken(token.subject, null, jwtService.getRoles(token))
            log.debug { "auth=$auth" }

            chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        } catch (e: Exception) {
            log.error(e) { "JWT exception" }
            chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.clearContext())
        }
    }
}
