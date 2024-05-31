package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.format.Format
import io.wrtn.kommons.logging.KLogging

class CoGifWriterTest: AbstractCoImageWriterTest() {

    companion object: KLogging()

    override val writer: CoImageWriter = CoGifWriter.Default
    override val imageFormat: Format = Format.GIF
}
