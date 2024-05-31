package com.sksamuel.scrimage.filters

import com.sksamuel.scrimage.AbstractScrimageTest
import com.sksamuel.scrimage.ImmutableImage
import io.bluetape4k.logging.KLogging

abstract class AbstractFilterTest: AbstractScrimageTest() {

    companion object: KLogging()

    protected fun loadResourceImage(imageName: String): ImmutableImage {
        return ImmutableImage.loader().fromResource("/scrimage/filters/$imageName")
    }
}
