package io.wrtn.kommons.captcha.config

import java.awt.Color
import java.awt.Font

data class CaptchaConfig(
    var width: Int = DEFAULT_WIDTH,
    var height: Int = DEFAULT_HEIGHT,
    var length: Int = DEFAULT_LENGTH,
    var noiseCount: Int = DEFAULT_NOISE_COUNT,
    var theme: CaptchaTheme = DEFAULT_THEME,
    val lightPalette: MutableList<Color> = DEFAULT_LIGHT_PALETTE,
    val darkPalette: MutableList<Color> = DEFAULT_DARK_PALETTE,
    var lightBackgroundColor: Color = DEFAULT_LIGHT_BG_COLOR,
    var darkBackgroundColor: Color = DEFAULT_DARK_BG_COLOR,
    val fontStyles: MutableList<Int> = DEFAULT_FONT_STYLES,
    val fontPaths: MutableList<String> = DEFAULT_FONTS,
    var fontSize: Int = (height * 0.8).toInt(),
) {

    companion object {
        const val DEFAULT_WIDTH = 200
        const val DEFAULT_HEIGHT = 80
        const val DEFAULT_LENGTH = 6
        const val DEFAULT_NOISE_COUNT = 4

        val DEFAULT_THEME = CaptchaTheme.LIGHT

        val DEFAULT_LIGHT_PALETTE = mutableListOf(
            Color.BLACK,
            Color.BLUE,
            Color.RED,
            Color.DARK_GRAY,
        )
        val DEFAULT_DARK_PALETTE = mutableListOf(
            Color.WHITE,
            Color.LIGHT_GRAY,
            Color.CYAN,
            Color.ORANGE,
            Color.YELLOW,
            Color.MAGENTA,
            Color.PINK
        )

        val DEFAULT_LIGHT_BG_COLOR = Color.WHITE
        val DEFAULT_DARK_BG_COLOR = Color(30, 30, 30)

        val DEFAULT_FONT_STYLES = mutableListOf(Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD or Font.ITALIC)
        val DEFAULT_FONTS = mutableListOf<String>()
        val DEFAULT_FONTS_IN_RESOURCE = mutableListOf(
            "/fonts/Roboto-Bold.ttf",
            "/fonts/Roboto-Italic.ttf",
            "/fonts/Roboto-Regular.ttf",
        )
    }

    val isDarkTheme: Boolean
        get() = theme == CaptchaTheme.DARK

    val isLightTheme: Boolean
        get() = theme == CaptchaTheme.LIGHT

    val themePalette: List<Color>
        get() = if (isDarkTheme) darkPalette else lightPalette

    val backgroundColor: Color
        get() = if (isDarkTheme) darkBackgroundColor else lightBackgroundColor
}
