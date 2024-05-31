package io.wrtn.kommons.images.coroutines.animated

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.wrtn.kommons.images.AbstractImageTest
import io.wrtn.kommons.io.readAllBytesSuspending
import io.wrtn.kommons.junit5.coroutines.runSuspendWithIO
import io.wrtn.kommons.junit5.folder.TempFolder
import io.wrtn.kommons.logging.KLogging
import io.wrtn.kommons.logging.debug
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path

class AsyncGif2WebpWriterTest: AbstractImageTest() {

    companion object: KLogging()

    private val useTempFolder = true

    @Test
    fun `convert animated gif to webp`(tempFolder: TempFolder) = runSuspendWithIO {
        val originBytes = Path.of("$BASE_PATH/animated.gif").readAllBytesSuspending()
        val gif2 = AnimatedGifReader.read(ImageSource.of(originBytes))

        val saved = if (useTempFolder) {
            gif2.forCoWriter(CoGif2WebpWriter.Default).write(tempFolder.createFile().toPath())
        } else {
            gif2.forCoWriter(CoGif2WebpWriter.Default).write(Path.of("$BASE_PATH/animated.webp"))
        }
        log.debug { "save animated.webp file to $saved" }
        saved.toFile().exists().shouldBeTrue()
        saved.toFile().length().shouldBeGreaterThan(0)
    }
}
