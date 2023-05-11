package io.bluetape4k.utils.jwt.repository

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import io.bluetape4k.utils.jwt.KeyChain
import java.util.*
import kotlin.concurrent.timer

abstract class AbstractKeyChainRepository: KeyChainRepository {

    companion object: KLogging() {
        /**
         * 기본 Refresh time (1분)
         */
        private const val DEFAULT_REFRESH_TIME_MILLIS = 60_000L
    }

    private var cachedCurrent: KeyChain? = null

    private var timer: Timer? = null

    init {
        timer = timer(this::class.java.name, true, DEFAULT_REFRESH_TIME_MILLIS, DEFAULT_REFRESH_TIME_MILLIS) {
            refreshCurrent()
        }
    }

    protected abstract fun doLoadCurrent(): KeyChain
    protected abstract fun doInsert(keyChain: KeyChain)

    override fun current(): KeyChain {
        if (cachedCurrent == null) {
            cachedCurrent = doLoadCurrent()
        }
        return cachedCurrent ?: error("Current keyChain을 가져올 수 없습니다. rotate를 먼저 수행해주세요")
    }

    protected fun refreshCurrent() {
        runCatching {
            cachedCurrent = doLoadCurrent()
        }.onFailure {
            log.warn(it) { "Fail to refresh current keyChain" }
        }
    }

    /**
     * 기존 토큰 만료 조건
     *
     * 1. keyChainTimeoutMillis 가 0 이하 = 기존 keychain 의 생성 시간과 현재 시간 관계 없이 무조건 만료
     * 2. keychain 의 생성 시각 + 만료시각이 현재시각보다 이전인 경우. (1시 생성 + 유효시간 30분인 keychain 은 1시 30분 이후에 만료된다.)
     *
     * @param keyChainTimeoutMillis key chain 의 유효 기간을 밀리초로 표현한 값 (0 이하의 값이면 만료된 것으로 간주합니다)
     * @return 만료 여부
     */
    protected fun isCurrentKeyChainExpired(keyChainTimeoutMillis: Long): Boolean =
        keyChainTimeoutMillis <= 0 || current().createdAt + keyChainTimeoutMillis < System.currentTimeMillis()

    protected fun changeCurrent(keyChain: KeyChain): Boolean {
        log.debug { "Change new keyChain. kid=${keyChain.id}" }
        var changed = false
        runCatching {
            doInsert(keyChain)
        }.onSuccess {
            cachedCurrent = keyChain
            changed = true
        }.onFailure {
            log.warn(it) { "Fail to change current keyChain. kid=${keyChain.id}" }
        }
        return changed
    }
}
