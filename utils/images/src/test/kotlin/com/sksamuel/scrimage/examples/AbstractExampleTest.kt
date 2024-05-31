package com.sksamuel.scrimage.examples

import com.sksamuel.scrimage.AbstractScrimageTest
import com.sksamuel.scrimage.ImmutableImage
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import java.io.File
import java.nio.file.Path

@TempFolderTest
abstract class AbstractExampleTest: AbstractScrimageTest() {
    companion object: KLogging() {
        const val EXAMPLE_BASE_PATH = "/scrimage/examples"

        const val saveResult = false

        fun getExampleImage(filename: String, scaleToWidth: Int = 640): ImmutableImage {
            return ImmutableImage.loader()
                .fromResource("$EXAMPLE_BASE_PATH/$filename")
                .scaleToWidth(scaleToWidth)
        }

        fun getTargetPath(tempFolder: TempFolder, filename: String): Path {
            return if (saveResult) {
                File(filename).toPath()
            } else {
                tempFolder.createFile().toPath()
            }
        }
    }
}
