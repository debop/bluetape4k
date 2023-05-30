package io.bluetape4k.data.redis.redisson.leader

import io.bluetape4k.core.requireNotBlank
import org.redisson.api.RedissonClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

inline fun <T> RedissonClient.runIfLeader(
    jobName: String,
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    crossinline action: () -> T,
): T {
    jobName.requireNotBlank("jobName")
    val leaderElection = RedissonLeaderElection(this, options)
    return leaderElection.runIfLeader(jobName) { action() }
}

inline fun <T> RedissonClient.runAsyncIfLeader(
    jobName: String,
    executor: Executor = ForkJoinPool.commonPool(),
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    crossinline action: () -> CompletableFuture<T>,
): CompletableFuture<T> {
    jobName.requireNotBlank("jobName")
    val leaderElection = RedissonLeaderElection(this, options)
    return leaderElection.runAsyncIfLeader(jobName, executor) { action() }
}
