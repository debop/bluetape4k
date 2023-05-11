package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.jwt.KeyChain
import io.bluetape4k.utils.jwt.composer.JwtComposer
import io.bluetape4k.utils.jwt.composer.JwtComposerDsl
import io.bluetape4k.utils.jwt.composer.composeJwt
import io.bluetape4k.utils.jwt.reader.JwtReader
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.SignatureAlgorithm

interface JwtProvider {

    companion object: KLogging()

    val signatureAlgorithm: SignatureAlgorithm

    fun currentKeyChain(): KeyChain

    fun rotate()

    fun findKeyChain(kid: String): KeyChain?

    fun composer(keyChain: KeyChain? = null): JwtComposer {
        return JwtComposer(keyChain ?: currentKeyChain())
    }

    fun compose(
        keyChain: KeyChain? = null,
        initializer: JwtComposerDsl.() -> Unit,
    ): String {
        return composeJwt(keyChain ?: currentKeyChain(), initializer)
    }

    fun parse(jwtString: String): JwtReader {
        jwtString.requireNotBlank("jwtString")
        log.trace { "Parse jwt string ... $jwtString" }

        val jws: Jws<Claims> = this.currentJwtParser().parseClaimsJws(jwtString)
        return JwtReader(jws)
    }
}
