package com.sksamuel.scrimage.examples

import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.nio.JpegWriter
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.logging.KLogging
import org.junit.jupiter.api.Test

class BoundExamples: AbstractExampleTest() {

    companion object: KLogging()

    @Test
    fun `bound image`(tempFolder: TempFolder) {
        val image = getExampleImage("input.jpg")

        // 이미지를 400x300 으로 bound 합니다. 기본은 ScaleMethod.BiCubic 입니다.
        image.bound(400, 300, ScaleMethod.FastScale)
            .forWriter(JpegWriter.Default)
            .write(getTargetPath(tempFolder, "bound_400_300.jpg"))

        image.bound(500, 200)
            .forWriter(JpegWriter.Default)
            .write(getTargetPath(tempFolder, "bound_500_200.jpg"))

        image.bound(300, 500)
            .forWriter(JpegWriter.Default)
            .write(getTargetPath(tempFolder, "bound_300_500.jpg"))
    }
}
