package io.bluetape4k.utils

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.support.closeTimeout
import java.util.LinkedList

object ShutdownQueue : KLogging() {

    private val queue: LinkedList<AutoCloseable> = LinkedList()

    init {
        Runtimex.addShutdownHook {
            var closeable: AutoCloseable?
            while (queue.pollLast().apply { closeable = this } != null) {
                log.debug { "Closing AutoCloseable instance ... $closeable" }
                closeable?.closeTimeout(3000L) { it.printStackTrace() }
                log.info { "Success to close AutoCloseable instance ... $closeable" }
            }
        }
    }

    /**
     * JVM Shutdown 시 자동 정리할 객체를 등록합니다
     */
    fun register(closeable: AutoCloseable) {
        log.debug { "JVM Shutdown 시 자동 정리할 객체를 등록합니다. $closeable" }
        queue.add(closeable)
    }
}