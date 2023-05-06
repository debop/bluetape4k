package io.bluetape4k.concurrent

import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

/**
 * [ForkJoinPool.commonPool]을 사용하는 [ExecutorService]
 */
object ForkJoinExecutor: ExecutorService by ForkJoinPool.commonPool()
