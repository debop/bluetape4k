package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.jwt.JwtConsts.DefaultSignatureAlgorithm
import io.bluetape4k.utils.jwt.KeyChain
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import java.security.KeyPair

class FixedJwtProvider private constructor(
    private val current: KeyChain,
): JwtProvider {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            signatureAlgorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
            keyPair: KeyPair = Keys.keyPairFor(signatureAlgorithm),
            kid: String,
        ): FixedJwtProvider {
            return FixedJwtProvider(KeyChain(signatureAlgorithm, keyPair, kid))
        }
    }

    override val signatureAlgorithm: SignatureAlgorithm = current.algorithm

    override fun currentKeyChain(): KeyChain {
        return current
    }

    override fun rotate() {
        throw UnsupportedJwtException("FixedJwtProvider does not support key rotation.")
    }

    override fun findKeyChain(kid: String): KeyChain? {
        return if (current.id == kid) current else null
    }
}
