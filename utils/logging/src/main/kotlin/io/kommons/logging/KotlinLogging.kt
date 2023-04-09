package io.kommons.logging

import io.kommons.logging.internal.KLoggerFactory
import org.slf4j.Logger

object KotlinLogging {

    /**
     * 이름이 [name]인 Logger ([org.slf4j.Logger]) 를 생성합니다.
     *
     * ```
     * val log = KotlinLogging.logger("mylogger")
     * ```
     *
     * @param name Logger name
     */
    fun logger(name: String): Logger = KLoggerFactory.logger(name)

    /**
     * [action]이 속한 package name이 Logger name이 됩니다.
     *
     * ```
     * val log = KotlinLogging.logger {}
     * ```
     *
     * @param action [action]이 속한 package name이 Logger name이 됩니다.
     */
    fun logger(action: () -> Unit): Logger = KLoggerFactory.logger(action)
}
