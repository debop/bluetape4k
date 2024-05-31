package io.bluetape4k.coroutines

import io.bluetape4k.support.requireNotEmpty
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select

/**
 * Print [Job] children tree.
 */
fun Job.printDebugTree(offset: Int = 0) {
    println(" ".repeat(offset) + this)

    children.forEach {
        it.printDebugTree(offset + 2)
    }

    if (offset == 0) println()
}

suspend fun <T> joinAny(vararg jobs: Job) {
    jobs.requireNotEmpty("jobs")
    select { jobs.forEach { it.onJoin { } } }
}

suspend fun Collection<Job>.joinAny() {
    requireNotEmpty("jobs")
    select { forEach { it.onJoin { } } }
}

suspend fun Collection<Job>.joinAnyAndCancelOthers() {
    requireNotEmpty("jobs")

    val firstAwaited = select { forEachIndexed { index, job -> job.onJoin { index } } }

    forEachIndexed { index, job ->
        if (index != firstAwaited)
            runCatching { job.cancel() }
    }
}
