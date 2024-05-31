package io.bluetape4k.io

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.folder.TempFolder
import io.bluetape4k.junit5.folder.TempFolderTest
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
@TempFolderTest
class FileCoroutinesTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
        private val faker = Fakers.faker
        private fun randomString(length: Int = 256): String = Fakers.fixedString(length)
        private fun randomStrings(size: Int = 20): List<String> = List(size) { randomString() }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write bytearray to file and read suspending`(
        tempDir: TempFolder,
        @RandomValue bytes: ByteArray,
    ) = runSuspendWithIO {
        val filename = Fakers.randomUuid().encodeBase62() + ".dat"
        val path = tempDir.createFile(filename).toPath()

        val written = path.writeSuspending(bytes)
        written shouldBeEqualTo bytes.size.toLong()

        val loaded = path.readAllBytesSuspending()
        loaded shouldBeEqualTo bytes
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write string list to file in coroutines`(tempDir: TempFolder) = runSuspendWithIO {
        val contents = randomStrings()
        val filename = Fakers.randomUuid().encodeBase62() + ".txt"
        val path = tempDir.createFile(filename).toPath()

        path.writeLinesSuspending(contents)

        val loaded = path.readAllLinesSuspending().toList()
        loaded.size shouldBeEqualTo contents.size
        loaded shouldBeEqualTo contents
    }
}
