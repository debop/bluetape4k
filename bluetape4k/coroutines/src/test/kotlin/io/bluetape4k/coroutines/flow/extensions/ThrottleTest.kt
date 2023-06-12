package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.coroutines.tests.assertEmpty
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration

/**
 * Kotlinx Coroutines 에서 공식적으로 제공하는
 * [kotlinx.coroutines.flow.debounce], [kotlinx.coroutines.flow.sample]과 유사한 기능을 제공하는 것이
 * [throttleLeading], [throttleTrailing] 이다.
 *
 * [kotlinx.coroutines.flow.sample]은 [ThrottleBehavior.TRAILING] 과 유사하게 Timer 발생 시 가장 최근에 방출된 요소를 방출한다.
 */
class ThrottleTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `compare throttling methods`() = runTest {
        //-----1-----2-----3-----4-----5-----6-----7-----8-----9-----10
        //--------------|--------------|----------------|--------------|
        range(1, 10)
            .onEach { delay(200) }
            .debounce(501L)
            .assertResult(10)                       // debounce 는 timeout 안에 emit 되는 값이 있다면 overwrite 한다 

        range(1, 10)
            .onEach { delay(200) }
            .sample(501L)
            .assertResult(2, 5, 7)

        range(1, 10)
            .onEach { delay(200) }
            .throttleLeading(501L)
            .assertResult(1, 4, 7, 10)

        range(1, 10)
            .onEach { delay(200) }
            .throttleTrailing(501L)
            .assertResult(3, 6, 9, 10)
    }

    @Nested
    inner class Debounce {

        @Test
        fun `debounce flow items`() = runTest {
            flow {
                emit(1)
                delay(90)
                emit(2)
                delay(90)
                emit(3)             // emit
                delay(1010)
                emit(4)             // emit
                delay(1010)
                emit(5)             // emit
            }
                .debounce(1000)
                .assertResult(3, 4, 5)

            flow {
                emit(1)
                delay(90)
                emit(2)
                delay(90)
                emit(3)             // emit
                delay(1010)
                emit(4)             // emit
                delay(1010)
                emit(5)             // not emit !!! in sample
            }
                .sample(1000)
                .assertResult(3, 4)
        }

        @Test
        fun `debounce flow item by timer`() = runTest {
            //-----1-----2-----3-----4-----5
            //---------|---------|---------|
            flow {
                repeat(5) {
                    emit(it + 1)
                    delay(it * 300L)
                }
            }
                .debounce(501)          // debounce 는 timeout 이 호출되면, 기존 값은 모두 버린다.
                .assertResult(3, 4, 5)

            //-----1-----2-----3-----4-----5
            //---------|---------|---------|
            flow {
                repeat(5) {
                    emit(it + 1)
                    delay(300)
                }
            }
                .sample(501)
                .assertResult(2, 4)
        }
    }

    @Nested
    inner class Sample {

        @Test
        fun `sampling flow item by timer`() = runTest {
            //-----1-----2-----3-----4-----5-----6-----7-----8-----9-----10
            //--------------|--------------|----------------|--------------|
            range(1, 10)
                .onEach { delay(200) }
                .sample(501L)
                .assertResult(2, 5, 7)
        }

        @Test
        fun `sample flow item`() = runTest {
            flow {
                emit(1)             // skip
                emit(2)             // deliver
                delay(501)     // 501

                emit(3)             // skip
                delay(99)      // 600

                emit(4)             // skip
                delay(100)     // 700

                emit(5)             // skip
                emit(6)             // deliver
                delay(301)     // 1001

                emit(7)             // deliver
                delay(500)     // 1501
            }
                .sample(500)
                .assertResult(2, 6, 7)
        }
    }

    @Nested
    inner class ThrottleLeading {

        @Test
        fun `throttle flow item`() = runTest {
            flow {
                emit(1)             // deliver
                emit(2)             // skip
                delay(501)     // 501

                emit(3)             // deliver
                delay(99)      // 600

                emit(4)             // skip
                delay(100)     // 700

                emit(5)             // skip
                emit(6)             // skip
                delay(301)     // 1001

                emit(7)             // deliver
                delay(500)     // 1501
            }
                .throttleLeading(500)
                .assertResult(1, 3, 7)
        }

        @Test
        fun `throttle leading with completed flow`() = runTest {
            //-----1-----2-----3-----4-----5-----6-----7-----8-----9-----10
            //--------------|--------------|----------------|-------------|

            // 1 - deliver (200)
            // 2 - skip    (400)
            // ---------------------------- 501
            // 3 - skip    (600)
            // 4 - deliver (800)
            // ---------------------------- 1002
            // 5 - skip    (1000)
            // 6 - skip    (1200)
            // 7 - deliver (1400)
            // ---------------------------- 1503
            // 8 - skip    (1600)
            // 9 - skip    (1800)
            // 10 - deliver (2000)
            // ---------------------------- 2004
            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading(501)
                .assertResult(1, 4, 7, 10)
        }

        @Test
        fun `throttle with complete and no delay`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading(0)
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no duration`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading { Duration.ZERO }
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no time`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading(0L)
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with null value`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .map { it.takeIf { it % 2 == 0 } }
                .throttleLeading(500L)
                .assertResult(null, 4, null, 10)  // 1, 4, 7, 10 
        }

        @Test
        fun `throttle single flow`() = runTest {
            flowOf(1)
                .throttleLeading(100)
                .assertResult(1)
        }

        @Test
        fun `throttle empty flow`() = runTest {
            emptyFlow<Int>()
                .throttleLeading(100)
                .assertEmpty()
        }

        @Test
        fun `throttle never flow`() = runTest {
            var hasValue = false

            val job = neverFlow()
                .throttleLeading(100)
                .onEach { hasValue = true }
                .launchIn(this)

            advanceTimeBy(1000)
            job.cancel()
            hasValue.shouldBeFalse()
        }

        @Test
        fun `throttle with failure upstream`() = runTest {
            flow {
                emit(1)
                throw RuntimeException("Boom!")
            }
                .throttleLeading(100)
                .test {
                    awaitError()
                }

            flow {
                emit(1)
                delay(200)
                emit(2)
                delay(200)
                throw RuntimeException("Boom!")
            }
                .throttleLeading(100)
                .test {
                    awaitItem() shouldBeEqualTo 1
                    awaitItem() shouldBeEqualTo 2
                    awaitError()
                }

            flow {
                emit(1)             // Should be published since it is first
                delay(100)
                emit(2)              // Should be skipped since error will arrive before the timeout expires
                delay(100)
                throw RuntimeException("Boom!")
            }
                .throttleLeading(500)
                .test {
                    awaitItem() shouldBeEqualTo 1
                    awaitError()
                }
        }

        @Test
        fun `throttle with timer which raise exception`() = runTest {
            range(1, 10)
                .throttleLeading { throw RuntimeException("Boom!") }
                .test {
                    awaitItem() shouldBeEqualTo 1
                    awaitError()
                }

            flow {
                emit(1)
                delay(100)
                emit(2)
                delay(400)
                emit(3)
            }
                .throttleLeading {
                    when (it) {
                        1    -> Duration.ofMillis(400)
                        3    -> throw RuntimeException("first")
                        else -> throw RuntimeException("second")
                    }
                }
                .test {
                    awaitItem() shouldBeEqualTo 1
                    awaitItem() shouldBeEqualTo 3
                    awaitError().message shouldBeEqualTo "first"
                }
        }

        @Test
        fun `throttle take`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading(500)
                .take(1)
                .assertResult(1)

            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading { throw RuntimeException("Boom!") }
                .take(1)
                .assertResult(1)
        }

        @Test
        fun `throttle cancellation`() = runTest {
            var count = 0

            range(1, 10)
                .onEach { delay(200) }
                .throttleLeading {
                    if (count++ % 2 == 0) {
                        throw CancellationException("$it")
                    } else {
                        Duration.ofMillis(500L)
                    }
                }
                .materialize()
                .test {
                    awaitItem() shouldBeEqualTo Event.Value(1)
                    awaitItem().errorOrThrow() shouldBeInstanceOf CancellationException::class
                    awaitComplete()
                }
        }
    }

    @Nested
    inner class ThrottleTrailing {
        @Test
        fun `throttle flow item A`() = runTest {
            // -1---2----3-
            // -@-----!--@-----!
            // -------2--------3
            flow {
                delay(100)
                emit(1)
                delay(300)      // 400
                emit(2)
                delay(400)      // 800
                emit(3)
                delay(100)      // 900
            }
                .throttleTrailing(500)
                .assertResult(2, 3)
        }

        @Test
        fun `throttle flow item B`() = runTest {
            // -1---2----3----4
            // -@-----!--@-----!
            // -------2--------4
            flow {
                delay(100)
                emit(1)
                delay(300)          // 400
                emit(2)
                delay(400)          // 800
                emit(3)
                delay(450)          // 1250
                emit(4)
            }
                .throttleTrailing(500)
                .assertResult(2, 4)
        }

        @Test
        fun `throttle flow item C`() = runTest {
            // -1---2----3------4|
            // -@-----!--@-----!
            // -------2--------3 4
            flow {
                delay(100)
                emit(1)
                delay(300)          // 400
                emit(2)
                delay(400)          // 800
                emit(3)
                delay(550)          // 1350
                emit(4)
            }
                .throttleTrailing(500)
                .assertResult(2, 3, 4)
        }

        @Test
        fun `throttle with complete and no delay A`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleTrailing(0)
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no delay B`() = runTest {
            range(1, 10)
                .throttleTrailing(0)
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no delay C`() = runTest {
            range(1, 10)
                .onEach { delay(100) }
                .throttleTrailing(Duration.ZERO)
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no delay D`() = runTest {
            range(1, 10)
                .onEach { delay(100) }
                .throttleTrailing { Duration.ZERO }
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with complete and no delay E`() = runTest {
            range(1, 10)
                .throttleTrailing { _ -> Duration.ZERO }
                .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @Test
        fun `throttle with null value`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleTrailing(500L)
                .assertResult(3, 6, 9, 10)

            range(1, 10)
                .onEach { delay(200) }
                .map { it.takeIf { it % 2 == 0 } }
                .throttleTrailing(500L)
                .assertResult(null, 6, null, 10)  // 3, 6, 9, 10
        }

        @Test
        fun `throttle single flow`() = runTest {
            flowOf(1)
                .throttleTrailing(100)
                .assertResult(1)
        }

        @Test
        fun `throttle empty flow`() = runTest {
            emptyFlow<Int>()
                .throttleTrailing(100)
                .assertEmpty()
        }

        @Test
        fun `throttle never flow`() = runTest {
            var hasValue = false

            val job = neverFlow()
                .throttleTrailing(100)
                .onEach { hasValue = true }
                .launchIn(this)

            advanceTimeBy(1000)
            job.cancel()
            hasValue.shouldBeFalse()
        }

        @Test
        fun `throttle with failure upstream`() = runTest {
            flow {
                emit(1)
                throw RuntimeException("Boom!")
            }
                .throttleTrailing(100)
                .test {
                    awaitError()
                }

            // 1-----2----X
            //  --1   --2
            flow {
                emit(1)
                delay(200)
                emit(2)
                delay(200)
                throw RuntimeException("Boom!")
            }
                .throttleTrailing(100)
                .test {
                    awaitItem() shouldBeEqualTo 1
                    awaitItem() shouldBeEqualTo 2
                    awaitError()
                }

            // 1-2-X
            //  ----X
            flow {
                emit(1)
                delay(100)
                emit(2)
                delay(100)
                throw RuntimeException("Boom!")
            }
                .throttleTrailing(400)
                .test {
                    awaitError()
                }
        }

        @Test
        fun `throttle with timer which raise exception`() = runTest {
            range(1, 10)
                .throttleTrailing { throw RuntimeException("Boom!") }
                .test {
                    awaitError()
                }

            // 1-2----3
            //  ----2 X
            flow {
                emit(1)
                delay(100)
                emit(2)
                delay(400)
                emit(3)
            }
                .throttleTrailing {
                    when (it) {
                        1    -> Duration.ofMillis(400)
                        3    -> throw RuntimeException("first")
                        else -> throw RuntimeException("second")
                    }
                }
                .test {
                    awaitItem() shouldBeEqualTo 2
                    awaitError().message shouldBeEqualTo "first"
                }

            // 1-2----3------4
            //  ----2  -----X
            flow {
                emit(1)
                delay(100)
                emit(2)
                delay(400)
                emit(3)
                delay(600)
                emit(4)
            }
                .throttleTrailing {
                    when (it) {
                        1    -> Duration.ofMillis(400)
                        3    -> throw RuntimeException("first")
                        else -> throw RuntimeException("second")
                    }
                }
                .test {
                    awaitItem() shouldBeEqualTo 2
                    awaitError().message shouldBeEqualTo "first"
                }
        }

        @Test
        fun `throttle take`() = runTest {
            range(1, 10)
                .onEach { delay(200) }
                .throttleTrailing(500)
                .take(1)
                .assertResult(3)

            range(1, 10)
                .onEach { delay(200) }
                .concatWith(flow { throw RuntimeException("Boom!") })
                .throttleTrailing(500)
                .take(1)
                .assertResult(3)
        }

        @Test
        fun `throttle cancellation`() = runTest {
            // --1--2--3--4--5--6--7--8--9--10
            var count = 1

            range(1, 10)
                .onEach { delay(200) }
                .throttleTrailing {
                    if (count++ % 2 == 0) {
                        throw CancellationException("$it")
                    } else {
                        Duration.ofMillis(500L)
                    }
                }
                .materialize()
                .test {
                    awaitItem() shouldBeEqualTo Event.Value(3)
                    awaitItem().errorOrThrow() shouldBeInstanceOf CancellationException::class
                    awaitComplete()
                }
        }
    }
}
