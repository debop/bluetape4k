package io.bluetape4k.core.support

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import java.util.*

class LocaleSupportTest {
    companion object : KLogging()

    @Test
    fun `get system default locale`() {
        if (Locale.getDefault() == Locale.KOREA) {
            Locale.KOREA.isDefault().shouldBeTrue()
        } else {
            Locale.KOREA.isDefault().shouldBeFalse()
        }
    }

    @Test
    fun `when null, return default`() {
        val nullValue: Locale? = null
        nullValue.orDefault() shouldBeEqualTo Locale.getDefault()
    }

    @Test
    fun `get parent locale`() {
        Locale.KOREA.parent shouldBeEqualTo Locale.KOREAN
        Locale.KOREAN.parent.shouldBeNull()

        Locale.US.parent shouldBeEqualTo Locale.ENGLISH
        Locale.UK.parent shouldBeEqualTo Locale.ENGLISH
    }

    @Test
    fun `get all parents`() {
        Locale.KOREA.parents shouldBeEqualTo listOf(Locale.KOREA, Locale.KOREAN)
        Locale.KOREAN.parents shouldContain Locale.KOREAN

        Locale.US.parents shouldBeEqualTo listOf(Locale.US, Locale.ENGLISH)
        Locale.ENGLISH.parents shouldContain Locale.ENGLISH
    }

    @Test
    fun `find locale files`() {
        val expected = listOf("msg_ko_KR", "msg_ko", "msg")

        val filenames = Locale.KOREA.calculateFilenames("msg")

        log.debug { "filenames=$filenames" }
        filenames shouldBeEqualTo expected
    }
}
