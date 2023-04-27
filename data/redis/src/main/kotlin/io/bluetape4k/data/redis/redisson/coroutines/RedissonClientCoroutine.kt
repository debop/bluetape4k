package io.bluetape4k.data.redis.redisson.coroutines

import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import org.redisson.api.*
import org.redisson.transaction.TransactionException
import java.time.LocalDate

suspend inline fun RedissonClient.withBatchAwait(
    options: BatchOptions = BatchOptions.defaults(),
    action: RBatch.() -> Unit
): BatchResult<*> {
    return createBatch(options).apply(action).executeAsync().awaitSuspending()
}

suspend inline fun RedissonClient.withTransactionAwait(
    options: TransactionOptions = TransactionOptions.defaults(),
    action: RTransaction.() -> Unit
) {
    val tx = createTransaction(options)
    try {
        action(tx)
        tx.commitAsync().awaitSuspending()
    } catch (e: TransactionException) {
        runCatching { tx.rollbackAsync().awaitSuspending() }
        throw e
    }
}

/**
 * Redisson은 Thread 기반의 Lock을 지원합니다.
 * Coroutines 환경에서 Lock을 사용하고자 한다면, Unique 한 Lock Id를 제공해야 합니다.
 * 만약 이때 Lock Id를 제공하지 않으면, 제대로 Unlock을 할 수 없습니다.
 *
 * @param lockName Redisson Lock 이름
 * @return Lock 을 구분하기 위한 Identifier
 */
fun RedissonClient.getLockId(lockName: String): Long {
    val epochDay = LocalDate.now().toEpochDay()
    val sequenceName = "bluetape4k:lock:$lockName:$epochDay"
    return getAtomicLong(sequenceName).andIncrement
}
