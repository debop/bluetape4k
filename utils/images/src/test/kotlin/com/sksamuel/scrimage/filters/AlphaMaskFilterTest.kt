package com.sksamuel.scrimage.filters

import com.sksamuel.scrimage.filter.AlphaMaskFilter
import com.sksamuel.scrimage.nio.PngWriter
import io.wrtn.kommons.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class AlphaMaskFilterTest: AbstractFilterTest() {

    companion object: KLogging()

    private val saveResult = false

    @Test
    fun `alphamask happy path`() {
        val origin = loadResourceImage("tiger.jpg")
        val mask = loadResourceImage("gradation.jpg")
        val expected = loadResourceImage("alphamask.png")

        val alphaMasked = origin
            .cover(512, 256)
            .filter(AlphaMaskFilter(mask, 3))

        if (saveResult) {
            alphaMasked.forWriter(PngWriter.MaxCompression).write("alphamask.png")
        }
        alphaMasked shouldBeEqualTo expected
    }
}
