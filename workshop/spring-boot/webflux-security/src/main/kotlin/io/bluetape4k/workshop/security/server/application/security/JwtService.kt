package io.bluetape4k.workshop.security.server.application.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${app.secret}") val secret: String,
    @Value("\${app.refresh}") val refresh: String,
) {

    companion object: KLogging()

    fun accessToken(username: String, expirationInMillis: Long, roles: Array<String>): String {
        return generate(username, expirationInMillis, roles, secret)
    }

    fun decodeAccessToken(accessToken: String): DecodedJWT {
        return decode(secret, accessToken)
    }

    fun refreshToken(username: String, expirationInMillis: Long, roles: Array<String>): String {
        return generate(username, expirationInMillis, roles, refresh)
    }

    fun decodeRefreshToken(refreshToken: String): DecodedJWT {
        return decode(refresh, refreshToken)
    }

    fun getRoles(decodedJWT: DecodedJWT): List<SimpleGrantedAuthority> {
        return decodedJWT
            .getClaim("role").asList(String::class.java)
            .map { role -> SimpleGrantedAuthority(role) }
    }


    private fun generate(username: String, expirationInMillis: Long, roles: Array<String>, signature: String): String {
        log.debug { "Generating token. signature=$signature, username=$username, roles=${roles.joinToString(", ")}" }

        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationInMillis))
            .withArrayClaim("role", roles)
            .sign(Algorithm.HMAC512(signature.toByteArray()))
    }

    private fun decode(signature: String, token: String): DecodedJWT {
        log.debug { "Decode token. signature=$signature, token=$token" }

        return JWT.require(Algorithm.HMAC512(signature.toByteArray()))
            .build()
            .verify(token.replace("Bearer ", ""))
    }
}
