package io.wrtn.kommons.captcha.utils

import io.wrtn.kommons.utils.Resourcex
import java.awt.Font

object FontProvider {

    fun loadAllFontsFromResource(resourcePaths: List<String>): List<Font> {
        return resourcePaths.mapNotNull { path -> loadFontFromResource(path) }
    }

    fun loadFontFromResource(resourcePath: String): Font? {
        return try {
            val fontStream = Resourcex.getInputStream(resourcePath)
            Font.createFont(Font.TRUETYPE_FONT, fontStream)
        } catch (e: Exception) {
            null
        }
    }
}
