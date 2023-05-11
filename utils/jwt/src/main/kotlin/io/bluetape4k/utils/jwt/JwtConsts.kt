package io.bluetape4k.utils.jwt

import io.bluetape4k.utils.jwt.repository.inmemory.InMemoryKeyChainRepository
import io.jsonwebtoken.SignatureAlgorithm

object JwtConsts {

    const val HEADER_TYPE_KEY = "typ"
    const val HEADER_TYPE_VALUE = "JWT"
    const val HEADER_KEY_ID = "kid"
    const val HEADER_ALGORITHM = "alg"

    const val DEFAULT_KEY_ROTATION_MINUTES = 0

    val DefaultKeyChainRepository by lazy { InMemoryKeyChainRepository() }
    val DefaultSignatureAlgorithm = SignatureAlgorithm.RS256
}
