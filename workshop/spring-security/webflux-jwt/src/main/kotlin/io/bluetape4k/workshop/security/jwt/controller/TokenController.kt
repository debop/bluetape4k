package io.bluetape4k.workshop.security.jwt.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class TokenController(@Autowired private val encoder: JwtEncoder) {

    companion object: KLogging()

    @PostMapping("/token")
    fun token(authentication: Authentication): String {
        val now = Instant.now()
        val expiry = 10 * 60 * 60L  // 10 hours

        val scope = authentication.authorities.joinToString(" ") { it.authority }
        log.debug { "user=${authentication.name} scope=$scope" }

        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiry))
            .subject(authentication.name)
            .claim("scope", scope)
            .build()

        val jwt = this.encoder.encode(JwtEncoderParameters.from(claims))
        val token = jwt.tokenValue
        log.debug { "user=${authentication.name} token=$token" }

        return token
    }
}
