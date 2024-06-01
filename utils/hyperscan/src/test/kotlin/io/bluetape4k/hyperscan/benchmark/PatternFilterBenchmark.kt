package io.bluetape4k.hyperscan.benchmark

import io.bluetape4k.hyperscan.PatternFilter
import io.bluetape4k.hyperscan.patternFilterOf
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.amshove.kluent.shouldNotBeEmpty
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.NANOSECONDS)
class PatternFilterBenchmark {

    companion object {
        private val patterns = listOf(
            Pattern.compile("The number is ([0-9]+)"),
            Pattern.compile("The number is ([0-9]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^The color is (blue|red|orange)"),
            Pattern.compile("^The color is (blue|red|orange)", Pattern.MULTILINE),
            Pattern.compile("something.else"),
            Pattern.compile("something.else", Pattern.DOTALL),
            Pattern.compile("match.THIS", Pattern.CASE_INSENSITIVE or Pattern.DOTALL),
            Pattern.compile("^숫자는 ([0-9]+)입니다"),
            Pattern.compile("^숫자는 ([0-9]+)입니다", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^이 색상은 (파랑|빨강|오렌지)색입니다"),
            Pattern.compile("^이 색상은 (파랑|빨강|오렌지)색입니다", Pattern.MULTILINE),
        )
    }

    // 모든 Thread 에서 공유됩니다. 
    @State(Scope.Benchmark)
    class ThreadState {
        val patternFilter: PatternFilter = patternFilterOf(patterns)
    }

    @Benchmark
    fun simpleTextPatternFilter(state: ThreadState) {
        val input = "The number is 1234"
        val matches = state.patternFilter.filter(input)
        matches.shouldNotBeEmpty()
    }

    @Benchmark
    fun utf8TextPatternFilter(state: ThreadState) {
        val input = "숫자는 1234입니다"
        val matches = state.patternFilter.filter(input)
        matches.shouldNotBeEmpty()
    }

    @Benchmark
    fun koreanPatternFilter(state: ThreadState) {
        val input = "이 색상은 빨강색입니다"
        val matches = state.patternFilter.filter(input)
        matches.shouldNotBeEmpty()
    }
}
