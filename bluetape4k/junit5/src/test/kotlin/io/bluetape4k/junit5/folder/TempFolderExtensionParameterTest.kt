package io.bluetape4k.junit5.folder

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TempFolderExtensionParameterTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    private val tempFilenames = HashSet<String>()
    private val tempDirnames = HashSet<String>()

    @AfterAll
    fun `모든 임시 파일이 삭제됨`() {
        tempFilenames.filter { Files.exists(Paths.get(it)) }.shouldBeEmpty()
    }

    @AfterAll
    fun `모든 임시 폴더가 삭제됨`() {
        tempDirnames.filter { Files.exists(Paths.get(it)) }.shouldBeEmpty()
    }

    @Test
    @ExtendWith(TempFolderExtension::class)
    fun `인자로 temporary folder를 받을 수 있다`(tempFolder: TempFolder) {
        tempFolder.createFile("foo.txt").exists().shouldBeTrue()
        tempFolder.createDirectory("bar").exists().shouldBeTrue()
    }

    @Test
    @TempFolderTest
    fun `새로 생성된 폴더의 부모 폴더는 root 폴더입니다`(tempFolder: TempFolder) {
        val root = tempFolder.root
        root.exists().shouldBeTrue()

        val dir = tempFolder.createDirectory("bar")
        dir.parentFile shouldBeEqualTo root
    }

    @RepeatedTest(REPEAT_SIZE)
    @ExtendWith(TempFolderExtension::class)
    fun `반복 수행되는 메소드에 대해 매번 temporary file이 생성됩니다`(tempFolder: TempFolder) {
        val file = tempFolder.createFile("foo.txt")
        file.exists().shouldBeTrue()

        log.debug { "Temp file=${file.absolutePath}" }

        tempFilenames shouldNotContain file.absolutePath
        tempFilenames.add(file.absolutePath)
    }

    @RepeatedTest(REPEAT_SIZE)
    @TempFolderTest
    fun `반복 수행되는 메소드에 대해 매번 temporary folder가 생성됩니다`(tempFolder: TempFolder) {
        val dir = tempFolder.createDirectory("bar")
        dir.exists().shouldBeTrue()

        log.debug { "Temp dir=${dir.absolutePath}" }

        tempDirnames shouldNotContain dir.absolutePath
        tempDirnames.add(dir.absolutePath)
    }
}
