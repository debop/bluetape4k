package com.sksamuel.scrimage

import io.wrtn.kommons.junit5.folder.TempFolderTest
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.utils.Resourcex
import java.awt.Font

@TempFolderTest
abstract class AbstractScrimageTest {
    companion object: KLogging()

    protected fun getFont(fontName: String = "Roboto-Regular.ttf", size: Int = 48): Font {
        return FontUtils.createTrueType(Resourcex.getInputStream("/scrimage/fonts/$fontName"), size)
    }
}
