package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.format.Format
import io.wrtn.kommons.logging.KLogging

class CoPngWriterTest: AbstractCoImageWriterTest() {

    companion object: KLogging()

    override val writer: CoImageWriter = CoPngWriter.MaxCompression
    override val imageFormat: Format = Format.PNG
}
