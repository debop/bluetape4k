package io.bluetape4k.images.fonts

import com.sksamuel.scrimage.FontUtils
import io.bluetape4k.utils.Resourcex
import java.awt.Font

const val DEFAULT_FONT_SIZE = 12

val DEFAULT_FONT = fontOf()

/**
 * [Font]를 생성합니다.
 *
 * @param style 폰트 스타일
 * @param size 폰트 크기
 * @return [Font] 인스턴스
 */
fun fontOf(
    style: Int = Font.PLAIN,
    size: Int = DEFAULT_FONT_SIZE,
): Font {
    return Font(Font.SANS_SERIF, style, size)
}

/**
 * True type [Font]를 생성합니다.
 *
 * @param fontName 폰트 이름
 * @param size 폰트 크기
 * @return [Font] 인스턴스
 */
fun createTrueTypeFont(
    fontName: String = "Roboto-Regular.ttf",
    size: Int = DEFAULT_FONT_SIZE,
): Font {
    val fontStream = Resourcex.getInputStream("/fonts/$fontName")
    return FontUtils.createTrueType(fontStream, size)
}
