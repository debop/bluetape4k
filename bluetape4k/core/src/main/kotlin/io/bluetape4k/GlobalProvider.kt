package io.bluetape4k

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.ShutdownQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 시스템 전역에서 공용으로 사용할 수 있는 객체를 제공합니다.
 */
object GlobalProvider: KLogging() {

    /**
     * Virtual Thread 용 Executor
     */
    val virtualThreadExecutor: ExecutorService by lazy {
        Executors.newVirtualThreadPerTaskExecutor().apply {
            ShutdownQueue.register(this)
        }
    }
}
