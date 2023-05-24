package io.bluetape4k.data.redis.redisson.leader.coroutines

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.redis.redisson.leader.RedissonLeaderElectionOptions
import org.redisson.api.RedissonClient

suspend fun <T> RedissonClient.runIfLeaderSuspending(
    jobName: String,
    options: RedissonLeaderElectionOptions = RedissonLeaderElectionOptions.Default,
    action: suspend () -> T,
): T {
    jobName.requireNotBlank("jobName")
    val leaderElection = RedissonCoLeaderElection(this, options)
    return leaderElection.runIfLeader(jobName, action)
}
