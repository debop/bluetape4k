package io.bluetape4k.junit5.output

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest

class InMemoryLogbackAppenderTest {

    companion object : KLogging()

    private lateinit var appender: InMemoryLogbackAppender

    @BeforeEach
    fun beforeEach() {
        appender = InMemoryLogbackAppender(InMemoryLogbackAppenderTest::class)
    }

    @AfterEach
    fun afterEach() {
        appender.stop()
    }

    @RepeatedTest(5)
    fun `capture logback log message`() {
        log.debug { "First message" }
        appender.lastMessage shouldBeEqualTo "First message"
        appender.size shouldBeEqualTo 1

        log.debug { "Second message" }
        appender.lastMessage shouldBeEqualTo "Second message"
        appender.size shouldBeEqualTo 2

        appender.clear()
        appender.size shouldBeEqualTo 0
        appender.lastMessage.shouldBeNull()
        appender.messages.shouldBeEmpty()
    }

    @RepeatedTest(5)
    fun `capture logback log message with info level`() {
        appender.messages.isEmpty().shouldBeTrue()
        log.info { "Information" }
        appender.messages.size shouldBeEqualTo 1
        appender.lastMessage shouldBeEqualTo "Information"
    }
}
