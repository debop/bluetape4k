package io.bluetape4k.images.coroutines

import com.sksamuel.scrimage.format.Format
import io.bluetape4k.images.AbstractImageTest
import io.bluetape4k.images.forCoWriter
import io.bluetape4k.images.immutableImageOf
import io.bluetape4k.images.immutableImageOfSuspending
import io.bluetape4k.io.writeAsync
import io.bluetape4k.io.writeSuspending
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.concurrency.VirtualthreadTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import kotlinx.coroutines.future.await
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import kotlin.system.measureTimeMillis

@TempFolderTest
abstract class AbstractCoImageWriterTest: AbstractImageTest() {

    companion object: KLogging()

    abstract val writer: CoImageWriter
    abstract val imageFormat: Format

    protected open val useTempFolder = true

    @ParameterizedTest
    @MethodSource("getImageFileNames")
    fun `use async image writer`(filename: String, tempFolder: TempFolder) = runSuspendWithIO {
        measureTimeMillis {
            val image = immutableImageOfSuspending(Path.of("$BASE_PATH/$filename.jpg"))

            val bytes = image.forCoWriter(writer).bytes()
            if (useTempFolder) {
                val dest = tempFolder.createFile("${filename}_compressed.$imageFormat")
                dest.toPath().writeSuspending(bytes)
            } else {
                Path.of("$BASE_PATH/${filename}_compressed.$imageFormat").writeAsync(bytes).await()
            }
        }.apply {
            log.info { "Compressed $filename.$imageFormat in $this ms" }
        }
    }

    @ParameterizedTest
    @MethodSource("getImageFileNames")
    fun `async image writer in multi job`(filename: String, tempFolder: TempFolder) = runSuspendWithIO {
        val image = immutableImageOfSuspending(Path.of("$BASE_PATH/$filename.jpg"))

        MultiJobTester()
            .numJobs(4)
            .roundsPerJob(2)
            .add {
                val bytes = image.forCoWriter(writer).bytes()
                val path = tempFolder.createFile().toPath()
                path.writeAsync(bytes).await()
                log.debug { "Save $filename.$imageFormat to $path" }
            }
            .run()
    }

    @ParameterizedTest
    @MethodSource("getImageFileNames")
    fun `async image writer in multi threading`(filename: String, tempFolder: TempFolder) {
        val image = immutableImageOf(Path.of("$BASE_PATH/$filename.jpg"))

        MultithreadingTester()
            .numThreads(4)
            .roundsPerThread(2)
            .add {
                val file = tempFolder.createFile()
                image.forWriter(writer).write(file)
                log.debug { "Save $filename.$imageFormat to $file" }
            }
            .run()
    }

    @ParameterizedTest
    @MethodSource("getImageFileNames")
    fun `async image writer in virtual threading`(filename: String, tempFolder: TempFolder) {
        val image = immutableImageOf(Path.of("$BASE_PATH/$filename.jpg"))

        VirtualthreadTester()
            .numThreads(4)
            .roundsPerThread(2)
            .add {
                val file = tempFolder.createFile()
                image.forWriter(writer).write(file)
                log.debug { "Save $filename.$imageFormat to $file" }
            }
            .run()
    }
}
