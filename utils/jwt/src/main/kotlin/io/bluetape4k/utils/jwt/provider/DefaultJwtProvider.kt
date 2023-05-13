package io.bluetape4k.utils.jwt.provider

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.info
import io.bluetape4k.utils.jwt.JwtConsts.DefaultKeyChainRepository
import io.bluetape4k.utils.jwt.JwtConsts.DefaultSignatureAlgorithm
import io.bluetape4k.utils.jwt.keychain.KeyChain
import io.bluetape4k.utils.jwt.keychain.repository.KeyChainRepository
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import kotlin.concurrent.timer

class DefaultJwtProvider private constructor(
    override val signatureAlgorithm: SignatureAlgorithm,
    private val repository: KeyChainRepository,
): JwtProvider {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            signatureAlgorithm: SignatureAlgorithm = DefaultSignatureAlgorithm,
            keyChainRepository: KeyChainRepository = DefaultKeyChainRepository,
        ): DefaultJwtProvider {
            log.info { "Create DefaultJwtProvider" }
            return DefaultJwtProvider(signatureAlgorithm, keyChainRepository)
        }
    }

    private val synchronizedObject = Any()

    private var currentKeyChain: KeyChain? = null
    private var timer: Timer? = null

    init {
        rotate()
        timer = timer(this.javaClass.name, true, 60_000, 60_000) {
            rotate()
        }
    }

    override fun currentKeyChain(): KeyChain {
        return currentKeyChain ?: repository.current().apply { currentKeyChain = this }
    }

    override fun rotate(): Boolean {
        log.info { "try rotate current KeyChain ..." }
        var rotated = false
        synchronized(synchronizedObject) {
            runCatching {
                val newKeyChain = createKeyChain()
                if (repository.rotate(newKeyChain)) {
                    log.info { "Rotate to new KeyChain. kid=${newKeyChain.id}" }
                    currentKeyChain = newKeyChain
                    rotated = true
                }
            }.onFailure { error ->
                log.error(error) { "Fail to rotate." }
            }
        }
        return rotated
    }

    override fun forcedRotate(): Boolean {
        log.info { "forced rotate current KeyChain ..." }
        var rotated = false
        synchronized(synchronizedObject) {
            runCatching {
                val newKeyChain = createKeyChain()
                if (repository.forcedRotate(newKeyChain)) {
                    log.info { "Rotate to new KeyChain. kid=${newKeyChain.id}" }
                    currentKeyChain = newKeyChain
                    rotated = true
                }
            }.onFailure { error ->
                log.error(error) { "Fail to rotate." }
            }
        }
        return rotated
    }

    override fun findKeyChain(kid: String): KeyChain? {
        log.debug { "find KeyChain. kid=$kid" }
        return if (currentKeyChain?.id == kid) currentKeyChain else repository.findOrNull(kid)
    }
}
