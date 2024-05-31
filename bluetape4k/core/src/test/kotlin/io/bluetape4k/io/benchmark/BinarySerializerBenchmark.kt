package io.bluetape4k.io.benchmark

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.serializer.BinarySerializers
import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.NANOSECONDS)
class BinarySerializerBenchmark {

    private val jdk = BinarySerializers.Jdk
    private val kryo = BinarySerializers.Kryo
    private val marshalling = BinarySerializers.Marshalling
    private val fury = BinarySerializers.Fury
    private val jsonMapper = jacksonObjectMapper()

    data class SimpleData(
        val id: Long,
        val name: String,
        val age: Int,
        val birth: Date,
        val biography: String,
        val zip: String,
        val address: String,
    ): Serializable

    val random: EnhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
        .objectPoolSize(100)
        .randomizationDepth(5)
        .charset(Charsets.UTF_8)
        .stringLengthRange(512, 1024)
        .collectionSizeRange(20, 40)
        .scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(true)
        .ignoreRandomizationErrors(true)
        .build()

    private lateinit var targets: List<SimpleData>

    @Setup
    fun setup() {
        targets = random.objects(SimpleData::class.java, 20).toList()
    }

    @Benchmark
    fun jdk() {
        with(jdk) {
            deserialize<List<SimpleData>>(serialize(targets))
        }
    }

    @Benchmark
    fun kryo() {
        with(kryo) {
            deserialize<List<SimpleData>>(serialize(targets))
        }
    }

    @Benchmark
    fun marshalling() {
        with(marshalling) {
            deserialize<List<SimpleData>>(serialize(targets))
        }
    }

    @Benchmark
    fun fury() {
        with(fury) {
            deserialize<List<SimpleData>>(serialize(targets))
        }
    }

    @Benchmark
    fun jsonMapper() {
        with(jsonMapper) {
            readValue<List<SimpleData>>(writeValueAsBytes(targets))
        }
    }
}
