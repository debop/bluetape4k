package io.bluetape4k.logging

import io.bluetape4k.logging.internal.KLoggerFactory
import org.slf4j.Logger
import kotlin.reflect.KClass

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

    /**
     * [clazz]의 name을 Logger name으로하는 Logger 를 생성합니다.
     *
     * @param clazz
     * @return
     */
    fun logger(clazz: Class<*>): Logger = logger(clazz.name)

    /**
     * [kclass]의 name을 Logger name으로하는 Logger 를 생성합니다.
     *
     * @param clazz
     * @return
     */
    fun logger(kclass: KClass<*>): Logger = logger(kclass.qualifiedName!!)
}
