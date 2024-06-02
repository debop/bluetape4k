package io.bluetape4k.images.filters

import io.bluetape4k.images.bytesSuspending
import io.bluetape4k.images.coroutines.CoJpegWriter
import io.bluetape4k.images.fonts.fontOf
import io.bluetape4k.images.forCoWriter
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.awt.Color

class WatermarkFilterTest: AbstractFilterTest() {

    companion object: KLogging()

    private val saveResult = false

    @Test
    fun `add cover watermark`() = runSuspendWithIO {
        val origin = loadResourceImage("debop.jpg")
        val coverWatermark = watermarkFilterOf("bluetape4k.io", type = WatermarkFilterType.COVER, alpha = 0.4)
        val marked = origin.filter(coverWatermark)

        val resultFilename = "debop_watermark_cover.jpg"
        if (saveResult) {
            marked.forCoWriter(CoJpegWriter.Default).write(resultFilename)
        }
        marked.bytesSuspending(CoJpegWriter.Default) shouldBeEqualTo loadResourceImageBytes(resultFilename)
    }

    @Test
    fun `add stamp watermark`() = runSuspendWithIO {
        val origin = loadResourceImage("debop.jpg")
        val stampWatermark = watermarkFilterOf(
            "bluetape4k.io",
            font = fontOf(size = 48),
            type = WatermarkFilterType.STAMP,
            alpha = 0.4,
        )
        val marked = origin.filter(stampWatermark)

        val resultFilename = "debop_watermark_stamp.jpg"
        if (saveResult) {
            marked.forCoWriter(CoJpegWriter.Default).write(resultFilename)
        }
        marked.bytesSuspending(CoJpegWriter.Default) shouldBeEqualTo loadResourceImageBytes(resultFilename)
    }

    @Test
    fun `add located watermark`() = runSuspendWithIO {
        val origin = loadResourceImage("debop.jpg")
        val font = fontOf(size = 24)
        val watermark = watermarkFilterOf(
            "created by bluetape4k.io",
            25,
            origin.height - 15,
            font,
            true,
            0.4,
            Color.WHITE
        )
        val marked = origin.filter(watermark)

        val resultFilename = "debop_watermark.jpg"
        if (saveResult) {
            marked.forCoWriter(CoJpegWriter.Default).write(resultFilename)
        }

        marked.bytesSuspending(CoJpegWriter.Default) shouldBeEqualTo loadResourceImageBytes(resultFilename)
    }
}
