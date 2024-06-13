package io.bluetape4k.junit5.folder

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TempFolder: ExtensionContext.Store.CloseableResource, Closeable {

    companion object: KLogging() {
        private const val PREFIX = "bluetake4k_"
    }

    private val rootPath: Path
    private val rootFolder: File
    val root: File get() = rootFolder

    init {
        try {
            rootPath = Files.createTempDirectory(PREFIX)
            rootFolder = rootPath.toFile()
            log.trace { "Create temporary root directory. [$rootPath]" }
        } catch (e: IOException) {
            throw TempFolderException("Fail to create temporary root directory for testing", e)
        }
    }

    /**
     * 임시 파일을 생성합니다.
     *
     * @return 생성된 임시 [File] 객체
     */
    fun createFile(): File {
        return try {
            Files.createTempFile(rootPath, PREFIX, null).toFile().apply {
                log.trace { "Create temporary file. file=[$this]" }
            }
        } catch (e: IOException) {
            throw TempFolderException("Fail to create temporary file.")
        }
    }

    fun createFile(filename: String): File {
        return try {
            val path = Paths.get(root.path, filename)
            Files.createFile(path).toFile().apply {
                log.trace { "Create temporary file. [$path]" }
            }
        } catch (e: IOException) {
            throw TempFolderException("Fail to create temporary file. filename=$filename")
        }
    }

    fun createDirectory(dir: String): File {
        return try {
            val path = Paths.get(root.path, dir)
            Files.createDirectory(path).toFile().apply {
                log.trace { "Create temporary sub directory. [$path]" }
            }
        } catch (e: IOException) {
            throw TempFolderException("Fail to create temporary sub directory. dir=$dir")
        }
    }

    override fun close() {
        runCatching { destroy() }
    }

    private fun destroy() {
        if (root.exists()) {
            log.trace { "Delete temporary folder. [$rootPath]" }
            try {
                root.deleteRecursively()
            } catch (e: Exception) {
                throw TempFolderException("Fail to delete temporary folder. [$rootPath]", e)
            }
        }
    }
}
