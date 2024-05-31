package com.sksamuel.scrimage

import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.Resourcex
import java.awt.Font

@TempFolderTest
abstract class AbstractScrimageTest {
    companion object: KLogging()

    protected fun getFont(fontName: String = "Roboto-Regular.ttf", size: Int = 48): Font {
        return FontUtils.createTrueType(Resourcex.getInputStream("/scrimage/fonts/$fontName"), size)
    }
}
