package io.bluetape4k.images.filters

import com.sksamuel.scrimage.filter.Filter
import com.sksamuel.scrimage.filter.WatermarkCoverFilter
import com.sksamuel.scrimage.filter.WatermarkFilter
import com.sksamuel.scrimage.filter.WatermarkStampFilter
import io.bluetape4k.images.fonts.DEFAULT_FONT
import io.bluetape4k.images.fonts.fontOf
import java.awt.Color
import java.awt.Font

/**
 * Watermark 용 [Filter]를 생성합니다.
 *
 * @see WatermarkCoverFilter
 * @see WatermarkStampFilter
 *
 * @param text
 * @param font
 * @param type          [WatermarkFilterType]
 * @param antiAlias
 * @param alpha
 * @param color
 * @return
 */
fun watermarkFilterOf(
    text: String,
    font: Font = DEFAULT_FONT,
    type: WatermarkFilterType = WatermarkFilterType.COVER,
    antiAlias: Boolean = true,
    alpha: Double = 0.1,
    color: Color = Color.WHITE,
): Filter = when (type) {
    WatermarkFilterType.COVER -> WatermarkCoverFilter(text, font, antiAlias, alpha, color)
    WatermarkFilterType.STAMP -> WatermarkStampFilter(text, font, antiAlias, alpha, color)
}

/**
 * [WatermarkFilter]를 생성합니다.
 *
 * @param text
 * @param x
 * @param y
 * @param font
 * @param antiAlias
 * @param alpha
 * @param color
 * @return
 */
fun watermarkFilterOf(
    text: String,
    x: Int,
    y: Int,
    font: Font = fontOf(),
    antiAlias: Boolean = true,
    alpha: Double = 0.1,
    color: Color = Color.WHITE,
): WatermarkFilter {
    return WatermarkFilter(text, x, y, font, antiAlias, alpha, color)
}
