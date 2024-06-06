package com.sksamuel.scrimage.filters

import com.sksamuel.scrimage.filter.WatermarkCoverFilter
import com.sksamuel.scrimage.filter.WatermarkFilter
import com.sksamuel.scrimage.filter.WatermarkStampFilter
import com.sksamuel.scrimage.nio.PngWriter
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.awt.Color

class WatermarkTest: AbstractFilterTest() {

    companion object: KLogging()

    // write 내용이 바뀔 시에 true로 변경한 후 테스트를 실행하면 새로운 이미지 파일이 생성됩니다.
    private val saveResult = false

    @Test
    fun `add repeted wartermark`() {
        val origin = loadResourceImage("debop.jpg")
        val font = getFont(size = 36)
        val coverWatermark = WatermarkCoverFilter("debop@bluetape4k.io", font, true, 0.1, Color.WHITE)
        val marked = origin.filter(coverWatermark)

        if (saveResult) {
            marked.forWriter(PngWriter.MaxCompression).write("debop_watermark_cover.png")
        }

        marked shouldBeEqualTo loadResourceImage("debop_watermark_cover.png")
    }

    @Test
    fun `add stamped watermark`() {
        val origin = loadResourceImage("debop.jpg")
        val font = getFont()
        val stampWatermark = WatermarkStampFilter("debop@bluetape4k.io", font, false, 0.2, Color.WHITE)
        val marked = origin.filter(stampWatermark)

        if (saveResult) {
            marked.forWriter(PngWriter.MaxCompression).write("debop_watermark_stamp.png")
        }

        marked shouldBeEqualTo loadResourceImage("debop_watermark_stamp.png")
    }

    @Test
    fun `add located watermark`() {
        val origin = loadResourceImage("debop.jpg")
        val font = getFont(size = 24)
        val watermark = WatermarkFilter(
            "debop@gmail.com",
            25, origin.height - 15,
            font,
            true,
            0.6,
            Color.WHITE
        )
        val marked = origin.filter(watermark)

        if (saveResult) {
            marked.forWriter(PngWriter.MaxCompression).write("debop_watermark.png")
        }

        marked shouldBeEqualTo loadResourceImage("debop_watermark.png")
    }
}
