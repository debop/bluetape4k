package io.github.kommons.logging

import io.github.kommons.logging.internal.KLoggerFactory
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

    fun logger(action: () -> Unit): Logger = KLoggerFactory.logger(action)
}
