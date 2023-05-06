package io.bluetape4k.data.redis.redisson.leader

import org.redisson.api.RedissonClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

fun <T> RedissonClient.runIfLeader(
    jobName: String,
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    action: () -> T,
): T {
    val leaderElection = RedissonLeaderElection(this, options)
    return leaderElection.runIfLeader(jobName, action)
}

fun <T> RedissonClient.runAsyncIfLeader(
    jobName: String,
    executor: Executor = ForkJoinPool.commonPool(),
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    action: () -> CompletableFuture<T>,
): CompletableFuture<T> {
    val leaderElection = RedissonLeaderElection(this, options)
    return leaderElection.runAsyncIfLeader(jobName, executor, action)
}
