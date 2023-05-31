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

/**
 * 원본 이미지를 [ratio] 만큼 scaling 을 수행합니다.
 *
 * @param ratio scaling 할 비율
 * @return Scaled [BufferedImage]
 */
fun BufferedImage.scale(ratio: Double): BufferedImage {
    ratio.requirePositiveNumber("ratio")

    val w = (this.width * ratio).toInt()
    val h = (this.height * ratio).toInt()
    val xScale = w.toDouble() / this.width
    val yScale = h.toDouble() / this.height

    return scale(xScale, yScale)
}

/**
 * 이미지를 Scaling 한다
 *
 * @param width        Scaled image의 width
 * @param height       Scaled image의 height
 * @param proportional 비례 적용 여부
 * @return scaled [BufferedImage]
 */
fun BufferedImage.scale(width: Int, height: Int, proportional: Boolean = true): BufferedImage {
    val xScale = width.toDouble() / this.width
    val yScale = height.toDouble() / this.height

    return if (proportional) {
        scale(xScale.coerceAtMost(yScale))
    } else {
        scale(xScale, yScale)
    }
}

fun BufferedImage.scale(xScale: Double, yScale: Double): BufferedImage {
    val transform = AffineTransform.getScaleInstance(xScale, yScale)
    val w = (this.width * xScale).toInt()
    val h = (this.height * yScale).toInt()

    return bufferedImageOf(w, h).also { scaled ->
        scaled.drawRenderedImage(this@scale, transform)
    }
}

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
