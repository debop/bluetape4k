package io.bluetape4k.redis.redisson.leader.coroutines

import io.bluetape4k.redis.redisson.leader.RedissonLeaderElectionOptions
import io.bluetape4k.support.requireNotBlank
import org.redisson.api.RedissonClient

suspend inline fun <T> RedissonClient.runIfLeaderSuspending(
    jobName: String,
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    crossinline action: suspend () -> T,
): T {
    jobName.requireNotBlank("jobName")

    val leaderElection = RedissonCoLeaderElection(this, options)
    return leaderElection.runIfLeader(jobName) { action() }
}
