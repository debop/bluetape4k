package io.kommons.logging.internal

import io.kommons.logging.KLogging
import org.slf4j.Logger

internal object KLoggerFactory {

    /**
     * [org.slf4j.Logger]를 생성합니다.
     *
     * @param name Logger name
     */
    fun logger(name: String): Logger = org.slf4j.LoggerFactory.getLogger(name)

    /**
     * [org.slf4j.Logger]를 생성합니다.
     *
     * @param action 해당 action 이 속한 package name이 logger name이 됩니다.
     */
    fun logger(action: () -> Unit): Logger = logger(KLoggerNameResolver.name(action))

    /**
     * [org.slf4j.Logger]를 생성합니다.
     *
     * @param clazz clazz 의 qualified name이 logger name이 됩니다.
     * @return
     */
    fun logger(clazz: Class<*>): Logger = logger(KLoggerNameResolver.name(clazz))

    /**
     * [org.slf4j.Logger]를 생성합니다.
     *
     * @param klogging [KLogging] 인스턴스
     * @return
     */
    fun logger(klogging: KLogging): Logger = logger(KLoggerNameResolver.name(klogging.javaClass))
}
