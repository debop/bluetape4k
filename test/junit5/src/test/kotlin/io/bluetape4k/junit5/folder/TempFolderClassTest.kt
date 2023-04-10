package io.bluetape4k.junit5.folder

import org.amshove.kluent.shouldBeFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TempFolderClassTest {
    @Test
    fun `임시 폴더 생성 후 close 시에 임시폴더는 삭제된다`() {
        val tempFolder = TempFolder()
        val rootFile = tempFolder.root

        tempFolder.createDirectory("tempDir")
        tempFolder.createFile("tempFile")
        tempFolder.createFile()

        tempFolder.close()

        rootFile.exists().shouldBeFalse()
    }

    @Test
    fun `유효하지 않는 폴더명으로 생성하기`() {
        val invalidDirName = "\\\\/:*?\\\"<>|/:"

        TempFolder().use { folder ->
            assertThrows<TempFolder.TempFolderException> {
                folder.createDirectory(invalidDirName)
            }
        }
    }
}
