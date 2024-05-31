package io.wrtn.kommons.images.filters

import com.sksamuel.scrimage.ImmutableImage
import io.wrtn.kommons.images.AbstractImageTest
import io.wrtn.kommons.images.immutableImageOf
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.utils.Resourcex

abstract class AbstractFilterTest: AbstractImageTest() {

    companion object: KLogging() {
        const val FILTERS_DIR = "/images/filters/"
    }

    protected fun loadResourceImageBytes(imageName: String): ByteArray {
        return Resourcex.getBytes("$FILTERS_DIR/$imageName")
    }

    protected fun loadResourceImage(imageName: String): ImmutableImage {
        return immutableImageOf(loadResourceImageBytes(imageName))
    }
}
