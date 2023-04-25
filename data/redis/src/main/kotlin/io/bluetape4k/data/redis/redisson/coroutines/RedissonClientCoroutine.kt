package io.bluetape4k.data.redis.redisson.coroutines

import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import org.redisson.api.BatchOptions
import org.redisson.api.BatchResult
import org.redisson.api.RBatch
import org.redisson.api.RTransaction
import org.redisson.api.RedissonClient
import org.redisson.api.TransactionOptions
import org.redisson.transaction.TransactionException

suspend inline fun RedissonClient.withBatchAsync(
    options: BatchOptions = BatchOptions.defaults(),
    action: RBatch.() -> Unit
): BatchResult<*> {
    return createBatch(options).apply(action).executeAsync().awaitSuspending()
}

suspend inline fun RedissonClient.withTransactionAsync(
    options: TransactionOptions = TransactionOptions.defaults(),
    action: RTransaction.() -> Unit
) {
    val tx = createTransaction(options)
    try {
        action(tx)
        tx.commitAsync().awaitSuspending()
    } catch (e: TransactionException) {
        tx.rollbackAsync().awaitSuspending()
        throw e
    }
}
