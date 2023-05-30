package io.bluetape4k.utils.images

import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO
import javax.imageio.ImageWriter

abstract class AbstractImageTest {

    companion object: KLogging()

    protected fun getImage(path: String): InputStream = Resourcex.getInputStream(path)!!

    protected fun getImageWriter(format: ImageFormat): ImageWriter =
        ImageIO.getImageWritersByFormatName(format.name).next()

    protected fun writeToFile(filename: String, format: ImageFormat, items: List<ByteArray>) {
        items.forEachIndexed { index, bytes ->
            val path = Paths.get("src/test/resources/images/${filename}_${index}.${format.name}")
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        }
    }
}
