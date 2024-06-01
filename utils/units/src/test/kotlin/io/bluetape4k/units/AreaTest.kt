package io.bluetape4k.units

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AreaTest {

    @Test
    fun `convert area`() {
        100.0.millimeter2().inMillimeter2() shouldBeEqualTo 100.0
        100.0.centimeter2().inCentimeter2() shouldBeEqualTo 100.0
        100.0.meter2().inMeter2() shouldBeEqualTo 100.0
    }

    @Test
    fun `display human string`() {
        100.0.millimeter2().toHuman() shouldBeEqualTo "100.0 mm^2"
        100.0.centimeter2().toHuman() shouldBeEqualTo "100.0 cm^2"
        100.0.meter2().toHuman() shouldBeEqualTo "100.0 m^2"
    }

    @Test
    fun `parse empty string`() {
        Area.parse(null) shouldBeEqualTo Area.NaN
        Area.parse("") shouldBeEqualTo Area.NaN
        Area.parse(" \t ") shouldBeEqualTo Area.NaN
    }

    @Test
    fun `parse valid string`() {
        Area.parse("144.4 mm^2") shouldBeEqualTo 144.4.millimeter2()
        Area.parse("144.4 cm^2") shouldBeEqualTo 144.4.centimeter2()
        Area.parse("144.4 m^2") shouldBeEqualTo 144.4.meter2()
    }

    @Test
    fun `parse invalid format`() {
        assertFailsWith<NumberFormatException> {
            Area.parse("123.4")
        }
        assertFailsWith<NumberFormatException> {
            Area.parse("123.4 MC^2")
        }
        assertFailsWith<NumberFormatException> {
            Area.parse("123.4.4.0.0 m^2")
        }
    }

    @Test
    fun `negate expression`() {
        -144.0.meter2().inMeter2() shouldBeEqualTo -144.0
        -144.0.millimeter2().inMeter2() shouldBeEqualTo -144.0 * 1e-6
    }

    @Test
    fun `area arithmetic`() {
        val a = 100.meter2()
        val b = 200.meter2()

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a
        a * 2 shouldBeEqualTo b
        2 * a shouldBeEqualTo b
        b / 2 shouldBeEqualTo a
    }
}
