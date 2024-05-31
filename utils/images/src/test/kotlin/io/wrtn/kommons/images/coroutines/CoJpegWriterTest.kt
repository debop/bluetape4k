package io.wrtn.kommons.images.coroutines

import com.sksamuel.scrimage.format.Format
import io.wrtn.kommons.logging.KLogging

class CoJpegWriterTest: AbstractCoImageWriterTest() {

    companion object: KLogging()

    override val writer: CoImageWriter = CoJpegWriter.Default
    override val imageFormat: Format = Format.JPEG
}
