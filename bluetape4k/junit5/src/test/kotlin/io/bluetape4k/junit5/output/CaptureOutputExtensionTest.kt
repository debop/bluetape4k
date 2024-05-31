package io.bluetape4k.junit5.output

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@CaptureOutput
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CaptureOutputExtensionTest {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach(output: OutputCapturer) {
        verifyOutput(output, "@BeforeEach")
    }

    @AfterEach
    fun afterEach(output: OutputCapturer) {
        verifyOutput(output, "@AfterEach")
    }

    @Test
    @Order(1)
    fun `capture system output`(output: OutputCapturer) {
        verifyOutput(output, "SYS OUT #1")
    }

    @Test
    @Order(2)
    fun `capture system error`(output: OutputCapturer) {
        verifyError(output, "SYS ERR #2")
    }

    @Test
    @Order(3)
    fun `capture system out and err`(output: OutputCapturer) {
        verifyOutput(output, "SYS OUT #3")
        verifyError(output, "SYS ERR #4")
    }

    private fun verifyOutput(output: OutputCapturer, expected: String) {
        output.capture() shouldNotContain expected

        println(expected)

        output.expect { it shouldContain expected }
        output.expect { it shouldNotContain expected.lowercase() }
    }

    private fun verifyError(output: OutputCapturer, expected: String) {
        output.capture() shouldNotContain expected

        System.err.println(expected)

        output.expect { it shouldContain expected }
        output.expect { it shouldNotContain expected.lowercase() }
    }
}
