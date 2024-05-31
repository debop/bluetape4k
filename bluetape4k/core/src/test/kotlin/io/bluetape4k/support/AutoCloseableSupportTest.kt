package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeoutException

class AutoCloseableSupportTest {

    companion object: KLogging()

    private val closeable = mockk<AutoCloseable>(relaxed = true)

    @BeforeEach
    fun beforeEach() {
        clearMocks(closeable)
    }

    @Test
    fun `close 시에 예외가 발생해도 끝내기`() {
        every { closeable.close() } throws RuntimeException("Boom!")

        Thread.sleep(1)
        closeable.closeSafe()
        Thread.sleep(1)

        verify(exactly = 1) { closeable.close() }
        confirmVerified(closeable)
    }

    @Test
    fun `close 시 timeout 적용`() {
        var captured: Throwable? = null
        every { closeable.close() } answers { Thread.sleep(1000) }

        Thread.sleep(1)
        closeable.closeTimeout(10) { e -> captured = e }
        Thread.sleep(1)

        captured.shouldNotBeNull()
        captured shouldBeInstanceOf TimeoutException::class

        verify(exactly = 1) { closeable.close() }
        confirmVerified(closeable)
    }

    @Test
    fun `use in AutoCloseable`() {
        closeable.use {
            // do something
            log.debug { "closable executed..." }
        }

        verify(exactly = 1) { closeable.close() }
        confirmVerified(closeable)
    }
}
