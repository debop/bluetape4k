package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageIOReader
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Resourcex
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertFailsWith

class WebpTest: AbstractWebpTest() {

    companion object: KLogging()

    @Test
    fun `read webp file`() = runSuspendWithIO {
        val image = loadImage("test.webp")

        log.debug { "metadata=\n${image.metadata.prettyPrint()}" }

        log.debug { "all tag=\n${image.metadata.tagsAsSequence().joinToString("\n")}" }

        image.width shouldBeEqualTo 2000
        image.height shouldBeEqualTo 2000
    }

    @Test
    fun `write webp file`() = runSuspendWithIO {
        val image = loadImage("spacedock.jpg").scale(0.5)

        image.bytes(WebpWriter.MAX_LOSSLESS_COMPRESSION) shouldBeEqualTo
                Resourcex.getBytes("/scrimage/webp/spacedock.webp")
    }

    @Test
    fun `no alpha image`() = runSuspendWithIO {
        val webpWriter = WebpWriter.DEFAULT.withoutAlpha()

        loadImage("alpha.png").bytes(webpWriter) shouldBeEqualTo
                Resourcex.getBytes("/scrimage/webp/noAlpha.webp")
    }

    @Test
    fun `dwebp should capture error on failure`() = runSuspendWithIO {
        val dwebpPath = WebpHandler.getBinaryPaths("dwebp")[2]
        log.debug { "dwebp path=$dwebpPath" }

        assertFailsWith<IOException> {
            ImmutableImage.loader().fromResource(dwebpPath)
        }.message!! shouldContain "BITSTREAM_ERROR"
    }

    @Test
    fun `load PNG, JPG Image with WebpImageReader`() = runSuspendWithIO {
        val img1 = ImmutableImage.loader()
            .fromResource("/scrimage/webp/issue245.jpg")

        img1.shouldNotBeNull()

        val img2 = ImmutableImage.loader()
            .withImageReaders(listOf(WebpImageReader(), ImageIOReader()))
            .fromResource("/scrimage/webp/issue245.jpg")

        img2.shouldNotBeNull()
    }
}
