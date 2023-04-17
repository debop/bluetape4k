package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChannelAsFlowExamples {

    companion object: KLogging()

    class SingleShotEventBus {
        companion object: KLogging()

        private val _events = Channel<Event>(2048, onBufferOverflow = BufferOverflow.SUSPEND)

        val events: Flow<Event> = _events.receiveAsFlow()

        suspend fun postEvent(event: Event) {
            _events.send(event)
            log.trace { "Send event. $event" }
        }
    }

    private lateinit var producerDispatcher: ExecutorCoroutineDispatcher
    private lateinit var consumerDispatcher: ExecutorCoroutineDispatcher

    @BeforeEach
    @OptIn(DelicateCoroutinesApi::class)
    fun setup() {
        producerDispatcher = newFixedThreadPoolContext(16, "producer")
        consumerDispatcher = newFixedThreadPoolContext(16, "consumer")
    }

    @AfterEach
    fun cleanup() {
        producerDispatcher.close()
        consumerDispatcher.close()
    }

    @Test
    fun `send event to channel receive as flow`() = runTest {

        val eventBus = SingleShotEventBus()
        val jobs = ArrayList<Job>()

        val totalProduced = AtomicLong(0L)
        val totalConsumed = AtomicLong(0L)

        jobs += List(10) {
            launch(producerDispatcher) {
                while (isActive) {
                    totalProduced.incrementAndGet()
                    eventBus.postEvent(Created)

                }
            }
        }
        jobs += List(10) {
            launch(producerDispatcher) {
                while (isActive) {
                    totalProduced.incrementAndGet()
                    eventBus.postEvent(Deleted)

                }
            }
        }
        val consumedJob = List(10) {
            launch(consumerDispatcher) {
                eventBus.events
                    .onEach { evt ->
                        totalConsumed.incrementAndGet()
                        log.trace { "Receive event. $evt" }
                    }
                    .collect()
            }
        }
        delay(100)
        jobs.forEach { it.cancelAndJoin() }

        delay(100)
        consumedJob.forEach { it.cancelAndJoin() }

        log.debug { "Produced: ${totalProduced.get()}, Consumed: ${totalConsumed.get()}" }
    }
}
