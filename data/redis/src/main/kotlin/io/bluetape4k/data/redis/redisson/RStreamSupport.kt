package io.bluetape4k.data.redis.redisson

import io.bluetape4k.core.requireNotEmpty
import org.redisson.api.RStream
import org.redisson.api.StreamMessageId
import org.redisson.api.stream.StreamAddArgs
import org.redisson.api.stream.StreamReadGroupArgs
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

fun <K, V> streamAddArgsOf(key: K, value: V): StreamAddArgs<K, V> {
    return StreamAddArgs.entry(key, value)
}

fun <K, V> streamAddArgsOf(vararg args: Pair<K, V>): StreamAddArgs<K, V> {
    return StreamAddArgs.entries(args.toMap())
}

fun <K, V> streamAddArgsOf(args: Map<K, V>): StreamAddArgs<K, V> {
    return StreamAddArgs.entries(args)
}

fun <K, V> RStream<K, V>.ackAllAsync(groupName: String, ids: Collection<StreamMessageId>): CompletableFuture<Long> {
    ids.requireNotEmpty("ids")
    return this.ackAsync(groupName, *ids.toTypedArray()).toCompletableFuture()
}

fun <K, V> RStream<K, V>.claimAllAsync(
    groupName: String,
    consumerName: String,
    idleTime: Duration = Duration.ZERO,
    ids: Collection<StreamMessageId>,
): CompletableFuture<Map<StreamMessageId, Map<K, V>>> {
    return claimAsync(
        groupName,
        consumerName,
        idleTime.toMillis(),
        TimeUnit.MILLISECONDS,
        *ids.toTypedArray(),
    ).toCompletableFuture()
}


fun <K, V> RStream<K, V>.fastClaimAllAsync(
    groupName: String,
    consumerName: String,
    idleTime: Duration = Duration.ZERO,
    ids: Collection<StreamMessageId>,
): CompletableFuture<List<StreamMessageId>> {
    return fastClaimAsync(
        groupName,
        consumerName,
        idleTime.toMillis(),
        TimeUnit.MILLISECONDS,
        *ids.toTypedArray(),
    ).toCompletableFuture()
}


fun <K, V> RStream<K, V>.readGroupAllAsync(
    groupName: String,
    consumerName: String,
    id: StreamMessageId,
    count: Int = 0,
    timeout: Duration = Duration.ZERO,
): CompletableFuture<Map<StreamMessageId, Map<K, V>>> {
    val args = StreamReadGroupArgs.greaterThan(id).count(count).timeout(timeout)
    return readGroupAsync(groupName, consumerName, args).toCompletableFuture()
}
