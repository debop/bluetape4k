package io.bluetape4k.junit5.benchmark

import io.bluetape4k.junit5.store
import io.bluetape4k.logging.KLogging
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class BenchmarkExtension(
    private val unit: TimeUnit = TimeUnit.MILLISECONDS
) : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    companion object : KLogging() {
        private const val REPORT_FORMAT = "Elapsed time[%s] for `%s`"
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        context.store(this.javaClass).put(context.requiredTestMethod, Stopwatch())
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val testMethod = context.requiredTestMethod
        val stopwatch = context.store(this.javaClass).get(testMethod, Stopwatch::class.java)
        val duration = stopwatch.elapsedTime(unit)

        context.publishReportEntry(REPORT_FORMAT.format(unit.name, testMethod.name), duration.toString())
    }

    internal class Stopwatch {
        private val start: Long = System.nanoTime()

        fun elapsedTime(unit: TimeUnit): Long =
            unit.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)
    }
}
