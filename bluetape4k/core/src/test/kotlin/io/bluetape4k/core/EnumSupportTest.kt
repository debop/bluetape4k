package io.bluetape4k.core

import io.bluetape4k.core.EnumSupportTest.DAYS.FRI
import io.bluetape4k.core.EnumSupportTest.DAYS.MON
import io.bluetape4k.core.EnumSupportTest.DAYS.SAT
import io.bluetape4k.core.EnumSupportTest.DAYS.SUN
import io.bluetape4k.core.EnumSupportTest.DAYS.THR
import io.bluetape4k.core.EnumSupportTest.DAYS.TUE
import io.bluetape4k.core.EnumSupportTest.DAYS.WED
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class EnumSupportTest {

    companion object: KLogging()

    enum class DAYS { MON, TUE, WED, THR, FRI, SAT, SUN }

    @Test
    fun `get enum map`() {
        DAYS::class.java.enumMap().keys shouldBeEqualTo setOf("MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN")
    }

    @Test
    fun `get enum list`() {
        DAYS::class.java.enumList() shouldBeEqualTo listOf(MON, TUE, WED, THR, FRI, SAT, SUN)
    }

    @Test
    fun `get enum by name`() {
        DAYS::class.java.getByName("MON") shouldBeEqualTo MON

        DAYS::class.java.getByName("Mon").shouldBeNull()
    }

    @Test
    fun `check valid enum name`() {
        DAYS::class.java.isValidName("MON").shouldBeTrue()
        DAYS::class.java.isValidName("Mon").shouldBeFalse()
    }
}
