package io.bluetape4k.idgenerators.ksuid

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Runtimex
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.ConcurrentHashMap

@RandomizedTest
class KsuidTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid`() {
        val ksuid = Ksuid.generate()

        log.debug { "Generated Ksuid=$ksuid" }
        log.debug { Ksuid.prettyString(ksuid) }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid multiple`() {
        val ids = List(100) { Ksuid.generate() }

        ids.distinct() shouldHaveSize ids.size
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid as parallel`() {
        val ids = List(100) { it }.parallelStream()
            .map { Ksuid.generate() }
            .toList()

        ids.distinct() shouldHaveSize ids.size
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid in multi threading`() {
        val idMap = ConcurrentHashMap<String, Int>()

        MultithreadingTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(16)
            .add {
                val id = Ksuid.generate()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid in virtual threading`() {
        val idMap = ConcurrentHashMap<String, Int>()

        VirtualthreadTester()
            .numThreads(2 * Runtimex.availableProcessors)
            .roundsPerThread(16)
            .add {
                val id = Ksuid.generate()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `generate ksuid in multi jobs`() = runTest {
        val idMap = ConcurrentHashMap<String, Int>()

        MultiJobTester()
            .numJobs(2 * Runtimex.availableProcessors)
            .roundsPerJob(16)
            .add {
                val id = Ksuid.generate()
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }
}
