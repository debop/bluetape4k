package io.bluetape4k.leader

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * 여러 Process, Thread에서 같은 작업이 동시, 무작위로 실행되는 것을 방지하기 위해
 * Leader 를 선출되면 독점적으로 작업할 수 있도록 합니다.
 */
interface AsyncLeaderElection {
    /**
     * 리더로 선출되면 [action]을 수행하고, 그렇지 않다면 수행하지 않습니다.
     *
     * @param lockName lock name - lock 획득에 성공하면 leader로 승격되는 것이다.
     * @param executor 비동기 작업에 수행할 Executor
     * @param action leader 로 승격되면 수행할 코드 블럭
     * @return 작업 결과
     */
    fun <T> runAsyncIfLeader(
        lockName: String,
        executor: Executor = ForkJoinPool.commonPool(),
        action: () -> CompletableFuture<T>,
    ): CompletableFuture<T>
}
