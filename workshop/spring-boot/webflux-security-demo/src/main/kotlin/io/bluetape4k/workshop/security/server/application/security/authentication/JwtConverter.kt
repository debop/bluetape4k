package io.bluetape4k.workshop.security.server.application.security.authentication

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.workshop.security.server.application.HttpExceptionFactory
import io.bluetape4k.workshop.security.server.application.login.LoginRequest
import io.bluetape4k.workshop.security.server.application.login.toUsernamePasswordAuthenticationToken
import jakarta.validation.Validator
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.codec.json.AbstractJackson2Decoder
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 로그인 시 제출된 username, password 정보를 [UsernamePasswordAuthenticationToken]으로 변환하는 컨버터입니다.
 *
 * @property jacksonDecoder
 * @property validator
 * @constructor Create empty Jwt converter
 */
@Component
class JwtConverter(
    private val jacksonDecoder: AbstractJackson2Decoder,
    private val validator: Validator,
): ServerAuthenticationConverter {

    companion object: KLogging()

    /**
     * Request Body 의 [LoginRequest] 정보를 파싱해서 [UsernamePasswordAuthenticationToken] 으로 변환한다.
     *
     * @param exchange
     * @return
     */
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> = mono {
        val loginRequest = getUsernameAndPassword(exchange!!)
            ?: throw HttpExceptionFactory.badRequest()

        if (validator.validate(loginRequest).isNotEmpty()) {
            throw HttpExceptionFactory.badRequest()
        }

        return@mono loginRequest.toUsernamePasswordAuthenticationToken()
    }

    private suspend fun getUsernameAndPassword(exchange: ServerWebExchange): LoginRequest? {
        val dataBuffer = exchange.request.body
        val type = ResolvableType.forClass(LoginRequest::class.java)

        return jacksonDecoder
            .decodeToMono(dataBuffer, type, MediaType.APPLICATION_JSON, mapOf())
            .onErrorResume {
                log.error(it) { "Fail to parse request body. ${exchange.request.body}" }
                Mono.empty<LoginRequest>()
            }
            .cast(LoginRequest::class.java)
            .awaitFirstOrNull()
    }
}
