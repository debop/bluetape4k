package io.bluetape4k.io

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderExtension
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@RandomizedTest
@ExtendWith(TempFolderExtension::class)
class FileSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3

        private val faker = Fakers.faker

        private fun randomString(length: Int = 256): String =
            Fakers.fixedString(length)

        private fun randomStrings(size: Int = 20): List<String> =
            fastList(size) { randomString() }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write bytearray to file and read async`(
        tempDir: TempFolder,
        @RandomValue bytes: ByteArray,
    ) {
        val filename = Fakers.randomUuid().encodeBase62() + ".dat"
        val path = tempDir.createFile(filename).toPath()

        path.writeAsync(bytes)
            .thenApply { written ->
                written shouldBeEqualTo bytes.size.toLong()
            }
            .thenCompose {
                path.readAllBytesAsync()
            }
            .thenAccept { loaded ->
                loaded shouldBeEqualTo bytes
            }
            .join()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write bytearray to file and read async in coroutines`(
        tempDir: TempFolder,
        @RandomValue bytes: ByteArray,
    ) = runSuspendWithIO {
        val filename = Fakers.randomUuid().encodeBase62() + ".dat"
        val path = tempDir.createFile(filename).toPath()

        val written = path.writeAsync(bytes).await()
        written shouldBeEqualTo bytes.size.toLong()

        val loaded = path.readAllBytesAsync().await()
        loaded shouldBeEqualTo bytes
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write string list to file`(
        tempDir: TempFolder,
    ) {
        val contents = randomStrings()
        val filename = Fakers.randomUuid().encodeBase62() + ".txt"
        val path = tempDir.createFile(filename).toPath()

        path.writeLinesAsync(contents)
            .thenCompose { written ->
                written shouldBeGreaterThan 0L
                path.readAllLinesAsync()
            }.thenAccept { loaded ->
                loaded shouldBeEqualTo contents
            }
            .join()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write string list to file in coroutines`(
        tempDir: TempFolder,
    ) = runSuspendWithIO {
        val contents = randomStrings()
        val filename = Fakers.randomUuid().encodeBase62() + ".txt"
        val path = tempDir.createFile(filename).toPath()

        val writtenSize = path.writeLinesAsync(contents).await()
        writtenSize shouldBeGreaterThan 0L

        val loaded = path.readAllLinesAsync().await()
        loaded shouldBeEqualTo contents
    }
}
