package io.bluetape4k.utils.jwt.repository

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.jwt.KeyChain
import java.time.Duration

/**
 * [KeyChain] 정보를 저장소에 저장하여, 분산환경에서 공유하거나, 오래된 JWT를 파싱하는데 사용할 수 있다
 */
interface KeyChainRepository {

    companion object: KLogging() {
        const val DEFAULT_CAPACITY = 10
    }

    /**
     * rotated key chain 의 최대 저장 갯수 (기본값은 DEFAULT_CAPACITY (10))
     */
    val capacity: Int

    /**
     * 현재 사용할 [KeyChain]을 가져옵니다.
     */
    fun current(): KeyChain

    /**
     * [kid]에 해당하는 KeyChain 을 가져옵니다. rotated 된 key chain으로 만든 jwt를 파싱할 때 사용합니다.
     */
    fun find(kid: String): KeyChain?

    /**
     * 새로운 [keyChain] 을 current key chain으로 사용하기 revoke를 수행합니다.
     *
     * @param keyChain 새롭게 대체될 [KeyChain]
     * @param keyChainTimeoutMillis key chain 의 유효 기간 (0 이면 무한대, 기본 값은 1일)
     * @return revoke 여부
     */
    fun revoke(keyChain: KeyChain, keyChainTimeoutMillis: Long): Boolean

    /**
     * 새로운 [keyChain] 을 current key chain으로 사용하기 revoke를 수행합니다.
     *
     * @param keyChain 새롭게 대체될 [KeyChain]
     * @param keyChainTimeout key chain 의 유효 기간 (0 이면 무한대, 기본 값은 1일)
     * @return revoke 여부
     */
    fun revoke(keyChain: KeyChain, keyChainTimeout: Duration = Duration.ofDays(1)): Boolean =
        revoke(keyChain, keyChainTimeout.toMillis())

    /**
     * 저장된 모든 KeyChain을 삭제합니다. NOTE: 테스트 시에만 사용하세요
     */
    fun clear()
}
