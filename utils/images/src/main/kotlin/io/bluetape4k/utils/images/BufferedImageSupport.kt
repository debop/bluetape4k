package io.bluetape4k.utils.images

import io.bluetape4k.core.requirePositiveNumber
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.ImageOutputStream


fun BufferedImage.write(format: ImageFormat, path: String): Boolean {
    return ImageIO.write(this, format.name, File(path))
}

fun BufferedImage.write(format: ImageFormat, file: File): Boolean {
    return ImageIO.write(this, format.name, file)
}

fun BufferedImage.write(format: ImageFormat, outputStream: OutputStream): Boolean {
    return ImageIO.write(this, format.name, outputStream)
}

fun BufferedImage.write(format: ImageFormat, outputStream: ImageOutputStream): Boolean {
    return ImageIO.write(this, format.name, outputStream)
}

fun BufferedImage.drawRenderedImage(source: BufferedImage, transform: AffineTransform) {
    useGraphics { graphics ->
        graphics.drawRenderedImage(source, transform)
    }
}

fun BufferedImage.drawImage(
    image: Image,
    transform: AffineTransform,
    observer: ImageObserver? = null,
) {
    useGraphics { graphics ->
        graphics.drawImage(image, transform, observer)
    }
}

fun BufferedImage.drawImage(
    image: Image,
    x: Int = 0,
    y: Int = 0,
    observer: ImageObserver? = null,
) {
    useGraphics { graphics ->
        graphics.drawImage(image, x, y, observer)
    }
}

fun BufferedImage.drawImage(
    image: Image,
    x: Int = 0,
    y: Int = 0,
    width: Int = this@drawImage.width,
    height: Int = this@drawImage.height,
    observer: ImageObserver? = null,
) {
    useGraphics { graphics ->
        graphics.drawImage(image, x, y, width, height, observer)
    }
}

inline fun BufferedImage.useGraphics(action: (graphics: Graphics2D) -> Unit) {
    val graphics = this.createGraphics()
    try {
        action(graphics)
    } finally {
        graphics.dispose()
    }
}

/**
 * 새로운 [BufferedImage]를 생성합니다.
 *
 * @param w width
 * @param h height
 * @return [BufferedImage] instance
 */
fun bufferedImageOf(w: Int, h: Int): BufferedImage {
    w.requirePositiveNumber("w")
    h.requirePositiveNumber("h")

    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val gd = ge.defaultScreenDevice
    val gc = gd.defaultConfiguration
    return gc.createCompatibleImage(w, h)
}

fun bufferedImageOf(inputStream: InputStream): BufferedImage = ImageIO.read(inputStream)
fun bufferedImageOf(inputStream: ImageInputStream): BufferedImage = ImageIO.read(inputStream)
fun bufferedImageOf(file: File): BufferedImage = ImageIO.read(file)
fun bufferedImageOf(url: URL): BufferedImage = ImageIO.read(url)
