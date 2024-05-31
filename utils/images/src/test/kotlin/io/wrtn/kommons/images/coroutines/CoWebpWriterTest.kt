package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.format.Format
import io.wrtn.kommons.logging.KLogging

class CoWebpWriterTest: AbstractCoImageWriterTest() {

    companion object: KLogging()

    override val writer: CoImageWriter = CoWebpWriter.Default
    override val imageFormat: Format = Format.WEBP
}
