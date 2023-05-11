package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.core.requireZeroOrPositiveNumber
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.bluetape4k.utils.jwt.JwtConsts.DEFAULT_KEY_ROTATION_MINUTES
import io.bluetape4k.utils.jwt.JwtConsts.DefaultKeyChainRepository
import io.bluetape4k.utils.jwt.JwtConsts.DefaultSignatureAlgorithm
import io.bluetape4k.utils.jwt.KeyChain
import io.bluetape4k.utils.jwt.repository.KeyChainRepository
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import kotlin.concurrent.timer

class DefaultJwtProvider private constructor(
    override val signatureAlgorithm: SignatureAlgorithm,
    private val repository: KeyChainRepository,
    keyRotationMinutes: Int,
): JwtProvider {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            signatureAlgorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
            keyChainRepository: KeyChainRepository = DefaultKeyChainRepository,
            keyRotationMinutes: Int = DEFAULT_KEY_ROTATION_MINUTES,
        ): DefaultJwtProvider {
            log.info { "Create DefaultJwtProvider" }
            keyRotationMinutes.requireZeroOrPositiveNumber("keyRotationMinutes")
            return DefaultJwtProvider(signatureAlgorithm, keyChainRepository, keyRotationMinutes)
        }
    }

    private val synchronizedObject = Any()
    private val keyChainTimeoutMillis = keyRotationMinutes * 60_000L

    private var currentKeyChain: KeyChain? = null
    private var timer: Timer? = null

    init {
        rotate()
        if (keyRotationMinutes > 0) {
            timer = timer(this.javaClass.name, true, keyChainTimeoutMillis, keyChainTimeoutMillis) {
                rotate()
            }
        }
    }

    override fun currentKeyChain(): KeyChain {
        return currentKeyChain ?: repository.current().apply { currentKeyChain = this }
    }

    override fun rotate() {
        log.info { "try rotate current KeyChain ..." }
        synchronized(synchronizedObject) {
            runCatching {
                val newKeyChain = KeyChain(signatureAlgorithm)
                if (repository.revoke(newKeyChain, keyChainTimeoutMillis)) {
                    log.info { "Rotate to new KeyChain. kid=${newKeyChain.id}" }
                    currentKeyChain = newKeyChain
                }
            }.onFailure { error ->
                log.error(error) { "Fail to rotate." }
            }
        }
    }

    override fun findKeyChain(kid: String): KeyChain? {
        log.debug { "find KeyChain. kid=$kid" }
        return if (currentKeyChain?.id == kid) currentKeyChain else repository.find(kid)
    }


}
