package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class ReplaySubjectTest {

    companion object: KLogging()

    @Test
    fun `replay collector`() = runTest {
        val subject = ReplaySubject<Int>(5)
        var replay1 = false
        var replay2 = false

        withSingleThread { dispatcher ->

            val job1 = launch(dispatcher) {
                subject.collect {
                    log.trace { "Subject1 collect: $it" }
                }
                log.debug { "Subject1 Done." }
            }
            val job2 = launch(dispatcher) {
                subject.collect {
                    log.trace { "Subject2 collect: $it" }
                }
                log.debug { "Subject2 Done." }
            }

            subject.emit(1)
            subject.emit(2)
            subject.emit(3)

            subject.complete()
            job1.join()
            job2.join()

            // replaySubject가 complete 되었습니다만, 모든 emit 된 내용을 replay 합니다.
            subject.collect {
                replay1 = true
                log.debug { "Collect after complete replaySubject. collect: $it" }
            }
            subject.collect {
                replay2 = true
                log.debug { "Collect after complete replaySubject. collect: $it" }
            }
            log.debug { "All Done." }
            replay1.shouldBeTrue()
            replay2.shouldBeTrue()
        }
    }
}
