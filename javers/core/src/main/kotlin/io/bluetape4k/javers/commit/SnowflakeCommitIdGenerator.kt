package io.bluetape4k.javers.commit

import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.idgenerators.snowflake.Snowflake
import io.bluetape4k.idgenerators.snowflake.Snowflakers
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.withLock
import org.javers.core.commit.CommitId
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier

class SnowflakeCommitIdGenerator(
    private val snowflake: Snowflake = Snowflakers.Global,
): Supplier<CommitId> {

    private val commits = unifiedMapOf<CommitId, Int>()
    private val counter = atomic(0)
    private val lock = ReentrantLock()

    fun getSeq(commitId: CommitId): Int =
        commits[commitId] ?: throw NoSuchElementException("Not found commitId [$commitId]")

    override fun get(): CommitId = lock.withLock {
        counter.incrementAndGet()
        val next = CommitId(nextId(), 0)
        commits[next] = counter.value
        next
    }

    private fun nextId(): Long = snowflake.nextId()
}
