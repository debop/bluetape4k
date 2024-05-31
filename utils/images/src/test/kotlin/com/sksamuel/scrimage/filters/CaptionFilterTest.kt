package com.sksamuel.scrimage.filters

import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.color.X11Colorlist
import com.sksamuel.scrimage.filter.CaptionFilter
import com.sksamuel.scrimage.filter.Padding
import com.sksamuel.scrimage.nio.PngWriter
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.awt.Color

class CaptionFilterTest: AbstractFilterTest() {

    private val saveResult = false
    private val font = getFont("NanumGothic.ttf", 14)

    @Test
    fun `place caption on image`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font,
            Color.WHITE,
            1.0,
            false,
            false,
            Color.WHITE,
            0.1,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_bottom_left.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_bottom_left.png")
    }

    @Test
    fun `place caption using full width`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font,
            Color.WHITE,
            1.0,
            true,
            true,
            Color.WHITE,
            0.1,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_full_width.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_full_width.png")
    }

    @Test
    fun `place caption using caption alpha and color`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font,
            Color.WHITE,
            1.0,
            true,
            false,
            X11Colorlist.Brown.awt(),
            0.4,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_color_alpha.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_color_alpha.png")
    }

    @Test
    fun `place caption using text alpha and color`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font,
            X11Colorlist.CadetBlue4.awt(),
            0.8,
            false,
            false,
            Color.WHITE,
            0.1,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_text_color_alpha.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_text_color_alpha.png")
    }

    @Test
    fun `allow setting size`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font.deriveFont(50f),
            X11Colorlist.CadetBlue4.awt(),
            0.8,
            false,
            false,
            Color.WHITE,
            0.1,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_font_size.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_font_size.png")
    }

    @Test
    fun `place caption using anti alias`() {
        val image = loadResourceImage("fish.jpg")
        val filter = CaptionFilter(
            "무서운 가물치를 그린 그림입니다",// "This is an example of a big scary mudsucking fish",
            Position.BottomLeft,
            font.deriveFont(50f),
            Color.WHITE,
            1.0,
            true,
            false,
            Color.WHITE,
            0.1,
            Padding(40, 40, 20, 20)
        )
        val filtered = image.filter(filter)

        if (saveResult) {
            filtered.forWriter(PngWriter.MaxCompression).write("fish_caption_anti_alias.png")
        }
        filtered shouldBeEqualTo loadResourceImage("fish_caption_anti_alias.png")
    }
}
