package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.bluetape4k.images.coroutines.animated.CoGif2WebpWriter
import io.bluetape4k.images.coroutines.animated.bytesSuspending
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.Resourcex
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class Gif2WebpTest: AbstractWebpTest() {

    companion object: KLogging()

    private val animatedPath = "/scrimage/webp/animated.gif"
    private val webpPath = "scrimage/webp/animated.webp"
    private val animatedBytes = Resourcex.getBytes(animatedPath)
    private val animatedImageSource = ImageSource.of(animatedBytes)

    @Test
    fun `convert gif animation to webp`() {
        val gif2 = AnimatedGifReader.read(animatedImageSource)
        val webpBytes = gif2.bytes(Gif2WebpWriter.DEFAULT)
        webpBytes shouldBeEqualTo Resourcex.getBytes(webpPath)
    }

    @Test
    fun `convert to webp using CoGif2WebpWriter`() = runSuspendWithIO {
        val animatedGif = AnimatedGifReader.read(animatedImageSource)
        val webpBytes = animatedGif.bytesSuspending(CoGif2WebpWriter.Default)
        webpBytes shouldBeEqualTo Resourcex.getBytes(webpPath)
    }
}
