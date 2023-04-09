package io.chungtape4k.core

import io.chungtape4k.logging.KLogging
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AssertFunctionsTest {

    companion object : KLogging()

    class TestClass

    @Test
    fun `assert without -ea`() {
        AssertFunctionsTest::class.java.classLoader.setClassAssertionStatus(
            AssertFunctionsTest::class.qualifiedName,
            false
        )
        AssertFunctionsTest::class.java.desiredAssertionStatus().shouldBeFalse()
    }


    @Test
    fun `assert with -ea`() {
        TestClass::class.java.classLoader.setClassAssertionStatus(TestClass::class.qualifiedName, true)
        TestClass::class.java.desiredAssertionStatus().shouldBeTrue()
    }

    @Test
    fun `assert not null`() {
        var x: Long? = null
        assertFailsWith<AssertionError> {
            x.assertNotNull("x")
        }

        x = 12L
        x.assertNotNull("x")
    }

    @Test
    fun `assert not empty for string`() {
        var x: String? = null
        assertFailsWith<AssertionError> {
            x.assertNotEmpty("x")
        }

        x = ""
        assertFailsWith<AssertionError> {
            x.assertNotEmpty("x")
        }

        x = "    "
        x.assertNotEmpty("x")

        x = "  \t "
        x.assertNotEmpty("x")
    }

    @Test
    fun `assert not blank for string`() {
        var x: String? = null
        assertFailsWith<AssertionError> {
            x.assertNotBlank("x")
        }

        x = ""
        assertFailsWith<AssertionError> {
            x.assertNotBlank("x")
        }

        x = "    "
        assertFailsWith<AssertionError> {
            x.assertNotBlank("x")
        }

        x = "  \t "
        assertFailsWith<AssertionError> {
            x.assertNotBlank("x")
        }
    }

    @Test
    fun `assert for not empty collection`() {
        var x: Collection<String>? = null
        assertFailsWith<AssertionError> {
            x.assertNotEmpty("x")
        }

        x = emptyList()
        assertFailsWith<AssertionError> {
            x.assertNotEmpty("x")
        }

        x = listOf("a", "b")
        x.assertNotEmpty("x")
    }
}
