package io.bluetape4k.concurrent.virtualthread

import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Virtual thread executor
 */
private val virtualThreadExecutor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

/**
 * Reactor에서 Virtual Thread를 사용하기 위한 [Scheduler]
 * Virtual thread를 사용하는 [ExecutorService]를 반환
 */
val Schedulers.virtualThread: Scheduler
    get() = Schedulers.fromExecutorService(virtualThreadExecutor)

/**
 * Reactor에서 Virtual Thread를 사용하기 위한 [Scheduler]
 * Virtual thread를 사용하는 새로운 [ExecutorService] 를 생성하여 반환
 */
val Schedulers.newVirtualThread: Scheduler
    get() = Schedulers.fromExecutorService(Executors.newVirtualThreadPerTaskExecutor())
