package io.bluetape4k.hyperscan.benchmark

import io.bluetape4k.hyperscan.wrapper.Expression
import io.bluetape4k.hyperscan.wrapper.Scanner
import io.bluetape4k.hyperscan.wrapper.compile
import io.bluetape4k.hyperscan.wrapper.scannerOf
import io.bluetape4k.logging.KLogging
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.NANOSECONDS)
class RegressionBenchmark {

    companion object: KLogging() {
        val expression = Expression("The quick brown fox jumps over the lazy dog")
        val db = expression.compile()
    }

    @State(Scope.Benchmark)
    class ThreadState {
        val scanner: Scanner = scannerOf(db)
    }

    @Benchmark
    fun benchmarkASCII(state: ThreadState) {
        val input = "The quick brown fox jumps over the lazy dog is an English-language " +
                "pangram—a sentence that contains all of the letters of the English alphabet."
        val matches = state.scanner.scan(db, input)
    }

    @Benchmark
    fun benchmarkUTF8(state: ThreadState) {
        val input = "\uD83D\uDE00The quick brown fox jumps over the lazy dog is an English-language " +
                "pangram—a sentence that contains all of the letters of the English alphabet and 한글도 포함"
        val matches = state.scanner.scan(db, input)
    }
}
