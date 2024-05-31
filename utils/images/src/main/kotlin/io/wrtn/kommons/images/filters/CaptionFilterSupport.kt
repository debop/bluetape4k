package io.wrtn.kommons.images.filters

import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.filter.CaptionFilter
import com.sksamuel.scrimage.filter.Padding
import io.wrtn.kommons.images.fonts.DEFAULT_FONT
import java.awt.Color
import java.awt.Font

/**
 * [CaptionFilter] 생성자
 *
 * @param text
 * @param position
 * @param font
 * @param textAlpha
 * @param antiAlias
 * @param fullWidth
 * @param color
 * @param captionAlpha
 * @param padding
 * @return
 */
fun captionFilterOf(
    text: String,
    position: Position = Position.BottomLeft,
    font: Font = DEFAULT_FONT,
    textAlpha: Double = 0.5,
    antiAlias: Boolean = false,
    fullWidth: Boolean = false,
    color: Color = Color.WHITE,
    captionAlpha: Double = 0.1,
    padding: Padding = Padding(20),
): CaptionFilter {
    return CaptionFilter(
        text,
        position,
        font,
        color,
        textAlpha,
        antiAlias,
        fullWidth,
        color,
        captionAlpha,
        padding
    )
}
