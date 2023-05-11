package io.bluetape4k.utils.jwt.repository.inmemory

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import io.bluetape4k.utils.jwt.KeyChain
import io.bluetape4k.utils.jwt.repository.AbstractKeyChainRepository
import io.bluetape4k.utils.jwt.repository.KeyChainRepository.Companion.DEFAULT_CAPACITY
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * [KeyChain] 정보를 In memory에 저장하고, rotated key chain을 관리합니다.
 * 단 분산환경에서는 사용하지 못합니다.
 *
 * @property capacity  rotated key chain 의 최대 저장 갯수 (기본값은 DEFAULT_CAPACITY (10))
 */
class InMemoryKeyChainRepository(
    override val capacity: Int = DEFAULT_CAPACITY,
): AbstractKeyChainRepository() {

    companion object: KLogging()

    private val keyChainStore = ConcurrentLinkedDeque<KeyChain>()

    override fun doLoadCurrent(): KeyChain {
        return keyChainStore.first
    }

    override fun doInsert(keyChain: KeyChain) {
        keyChainStore.addFirst(keyChain)
    }

    override fun find(kid: String): KeyChain? {
        return keyChainStore.find { it.id == kid }
    }

    override fun revoke(keyChain: KeyChain, keyChainTimeoutMillis: Long): Boolean {
        log.debug { "Revoke KeyChain. kid=${keyChain.id}" }
        if (keyChainStore.isEmpty()) {
            return changeCurrent(keyChain)
        }

        val currentKeyChain = current()

        // current KeyChain이 아직 유효하다면 굳이 revoke 하지 않는다
        if (!isCurrentKeyChainExpired(keyChainTimeoutMillis)) {
            return false
        }

        if (currentKeyChain.id != keyChain.id) {
            return changeCurrent(keyChain).apply {
                if (keyChainStore.size > capacity) {
                    log.debug { "Remove oldest keychain ..." }
                    keyChainStore.removeLast()
                }
            }
        }
        return false
    }

    override fun clear() {
        log.warn { "저장된 모든 KeyChain을 삭제합니다. NOTE: 테스트 시에만 사용하세요" }
        keyChainStore.clear()
    }
}
