package io.bluetape4k.io.benchmark

import com.github.luben.zstd.Zstd
import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.compressor.ZstdCompressor
import io.bluetape4k.utils.Resourcex
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.NANOSECONDS)
class CompressBenchmark {

    private val snappy = Compressors.Snappy
    private val lz4 = Compressors.LZ4
    private val gzip = Compressors.GZip
    private val deflate = Compressors.Deflate
    private val zstd = Compressors.Zstd
    private val zstd_min = ZstdCompressor(Zstd.minCompressionLevel())
    private val brotli = Compressors.Brotli

    // Apple Silicon 에서는 실행이 안됩니다.
    //private val brotli = Compressors.Brotli

    private lateinit var randomString: String

    @Setup
    fun setup() {
        randomString = Resourcex.getString("files/Utf8Samples.txt")
    }

    @Benchmark
    fun snappy() {
        with(snappy) {
            decompress(compress(randomString))
        }
    }

    @Benchmark
    fun lz4() {
        with(lz4) {
            decompress(compress(randomString))
        }
    }

    @Benchmark
    fun gzip() {
        with(gzip) {
            decompress(compress(randomString))
        }
    }

    @Benchmark
    fun deflate() {
        with(deflate) {
            decompress(compress(randomString))
        }
    }


    @Benchmark
    fun zstd() {
        with(zstd) {
            decompress(compress(randomString))
        }

    }

    @Benchmark
    fun zstd_min() {
        with(zstd_min) {
            decompress(compress(randomString))
        }
    }

    @Benchmark
    fun brotli() {
        brotli.decompress(brotli.compress(randomString))
    }
}
