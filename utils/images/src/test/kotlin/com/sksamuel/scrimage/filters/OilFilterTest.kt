package com.sksamuel.scrimage.filters

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.filter.OilFilter
import com.sksamuel.scrimage.nio.PngWriter
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OilFilterTest: AbstractFilterTest() {

    companion object: KLogging()

    private val saveResult = false

    private fun getOriginal(): ImmutableImage = loadResourceImage("bird_small.png")

    private val oilFilter = OilFilter()

    @Test
    fun `filter output matches expected`() {
        val oiler = getOriginal().filter(oilFilter)

        if (saveResult) {
            oiler.forWriter(PngWriter.MaxCompression).write("bird_small_oil.png")
        }
        oiler shouldBeEqualTo loadResourceImage("bird_small_oil.png")
    }

    @ParameterizedTest(name = "oil filters range={0}, level=4")
    @ValueSource(ints = [6, 8, 10, 12])
    fun `support ranges`(range: Int) {
        verifyOilFilter(range, 4)
    }

    @ParameterizedTest(name = "oil filters range=6, level={0}")
    @ValueSource(ints = [4, 12, 20, 28])
    fun `support levels`(level: Int) {
        verifyOilFilter(6, level)
    }

    private fun verifyOilFilter(range: Int, level: Int) {
        val oiled = getOriginal().filter(OilFilter(range, level))
        val oiledImagePath = "bird_small_oil_${range}_${level}.png"

        if (saveResult) {
            oiled.forWriter(PngWriter.MaxCompression).write(oiledImagePath)
        }
        oiled shouldBeEqualTo loadResourceImage(oiledImagePath)
    }
}
