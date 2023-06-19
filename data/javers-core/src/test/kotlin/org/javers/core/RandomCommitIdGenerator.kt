package org.javers.core

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.idgenerators.snowflake.Snowflakers
import org.javers.core.commit.CommitId
import java.util.function.Supplier

class RandomCommitIdGenerator: Supplier<CommitId> {

    companion object: KLogging()

    private val commits = hashMapOf<CommitId, Int>()
    private val snowflake = Snowflakers.Global
    private var counter = 0

    fun getSeq(commitId: CommitId): Int =
        commits[commitId] ?: throw NoSuchElementException("Not found commitId[$commitId]")

    override fun get(): CommitId {
        synchronized(this) {
            counter++
            val next = CommitId(snowflake.nextId(), 0)
            commits[next] = counter

            log.trace { "Generate random CommitId. next commitId=$next, counter=$counter" }
            return next
        }
    }

}
