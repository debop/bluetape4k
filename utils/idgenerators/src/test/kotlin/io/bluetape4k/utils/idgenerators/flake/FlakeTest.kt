package io.bluetape4k.utils.idgenerators.flake

import io.bluetape4k.codec.encodeHexString
import io.bluetape4k.collections.eclipse.FastList
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.time.Clock
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class FlakeTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
        private const val ID_SIZE = 100
        private const val TEST_COUNT = Short.MAX_VALUE * 4
    }

    private val flake = Flake()

    @RepeatedTest(REPEAT_SIZE)
    fun `generate flake id`() {

        val ids = FastList(3) { flake.nextId() }

        ids[1] shouldNotBeEqualTo ids[0]
        ids[2] shouldNotBeEqualTo ids[1]
        ids[0] shouldNotBeEqualTo ids[2]

        ids.forEach {
            log.trace { "id= ${Flake.asHexString(it)}, ${Flake.asComponentString(it)}" }
        }
        ids.forEach {
            log.trace { "id as Long=${it.encodeHexString()}" }
        }
        ids.forEach {
            log.trace { "id as Base62=${Flake.asBase62String(it)}" }
        }
    }

    @Test
    fun `sequence increment`() {
        val nodeIdentifier: () -> Long = { 123456789L }
        val clock = Clock.tick(Clock.systemUTC(), Duration.ofMinutes(1))
        val flake = Flake(nodeIdentifier, clock)


        val ids = FastList(ID_SIZE) {
            Flake.asBase62String(flake.nextId())
        }
        ids.forEachIndexed { index, id ->
            log.trace { "id[$index]=$id" }
        }
        ids shouldHaveSize ID_SIZE
        ids.distinct() shouldHaveSize ID_SIZE
        ids.sorted() shouldBeEqualTo ids
    }

    @Test
    fun `generate more max sequence`() {
        repeat(TEST_COUNT) {
            flake.nextId()
        }
    }

    @Test
    fun `sequence reset`() {
        val seq1 = flake.nextId().copyOfRange(14, 15)
        Thread.sleep(10)
        val seq2 = flake.nextId().copyOfRange(14, 15)

        seq2 shouldBeEqualTo seq1
    }

    @Test
    fun `generate id in multi threading`() {
        val flake = Flake()
        val idMap = ConcurrentHashMap<String, Int>()

        MultithreadingTester().numThreads(100).roundsPerThread(1000)
            .add {
                val id = Flake.asBase62String(flake.nextId())
                idMap.putIfAbsent(id, 1).shouldBeNull()
            }
            .run()
    }

    @Test
    fun `generate id in corotunes`() = runSuspendTest(Dispatchers.Default) {
        val tasks = FastList(ID_SIZE) {
            async {
                flake.nextId()
            }
        }
        val ids = tasks.awaitAll()
        ids shouldHaveSize ID_SIZE
        ids.distinct() shouldHaveSize ID_SIZE
    }
}
