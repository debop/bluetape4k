package io.bluetape4k.data.javers.commit

import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.utils.idgenerators.snowflake.Snowflake
import io.bluetape4k.utils.idgenerators.snowflake.Snowflakers
import kotlinx.atomicfu.atomic
import org.javers.core.commit.CommitId
import java.util.function.Supplier

class SnowflakeCommitIdGenerator(
    private val snowflake: Snowflake = Snowflakers.Global,
): Supplier<CommitId> {

    private val commits = unifiedMapOf<CommitId, Int>()
    private val counter = atomic(0)
    private val syncObj = Any()

    fun getSeq(commitId: CommitId): Int =
        commits[commitId] ?: throw NoSuchElementException("Not found commitId [$commitId]")

    override fun get(): CommitId = synchronized(syncObj) {
        counter.incrementAndGet()
        val next = CommitId(nextId(), 0)
        commits[next] = counter.value
        next
    }

    private fun nextId(): Long = snowflake.nextId()
}
