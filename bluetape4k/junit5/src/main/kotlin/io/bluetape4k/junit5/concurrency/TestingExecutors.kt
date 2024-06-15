package io.bluetape4k.junit5.concurrency

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

object TestingExecutors {

    fun newScheduledExecutorService(corePoolSize: Int = 0, useVirtualThread: Boolean = true): ScheduledExecutorService {
        return if (useVirtualThread) {
            Executors.newScheduledThreadPool(corePoolSize, newVirtualThreadFactory())
        } else {
            Executors.newScheduledThreadPool(corePoolSize)
        }
    }

    fun newExecutorService(corePoolSize: Int = 0, useVirtualThread: Boolean = true): ExecutorService {
        return if (useVirtualThread) {
            Executors.newScheduledThreadPool(corePoolSize, newVirtualThreadFactory())
        } else {
            Executors.newScheduledThreadPool(corePoolSize)
        }
    }

    fun newVirtualThreadFactory(name: String = "ofVirtual-"): ThreadFactory {
        return Thread.ofVirtual().name(name, 0).factory()
    }

    fun newVirtualThreadPerTaskExecutor(): ExecutorService {
        return Executors.newThreadPerTaskExecutor(newVirtualThreadFactory())
    }
}
