package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class SystemPropertySupportTest {

    companion object: KLogging()

    @Test
    fun `get and set with system property`() {

        // 존재하지 않는 System Property 값
        sysProperty["not-exists"].shouldBeEmpty()

        // `user.dir` 는 존재하는 System Property 값
        sysProperty["user.dir"].shouldNotBeNull()

    }

    @Test
    fun `get and set with custom property`() {
        // Custom system property
        sysProperty["kommons.custom.property"] = "kommons"
        sysProperty["kommons.custom.property"] shouldBeEqualTo "kommons"
    }

    @Test
    fun `null or empty key with system property`() {
        assertFailsWith<IllegalArgumentException> {
            sysProperty[""] = "value"
        }
    }
}
