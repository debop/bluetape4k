package io.bluetape4k.io

import java.io.File
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 파일 경로에서 확장자를 제거합니다.
 */
fun Path.removeFileExtension(): String {
    val filename = toString()
    val pos = filename.indexOfExtension()
    return if (pos < 0) filename else filename.substring(0, pos)
}

private fun String.indexOfExtension(): Int {
    val extensionPos = lastIndexOf(EXTENSION_SEPARATOR)
    val lastSeparator = indexOfLastSeparator()
    return if (lastSeparator > extensionPos) -1 else extensionPos
}

private fun String.indexOfLastSeparator(): Int =
    lastIndexOfAny(charArrayOf(UNIX_SEPARATOR, WINDOW_SEPARATOR))

fun Path.combine(vararg subpaths: String): Path =
    Paths.get(this.toString(), *subpaths)

fun Path.combine(vararg subpaths: Path): Path =
    Paths.get(this.toString(), *subpaths.map { it.toString() }.toTypedArray())

fun Path.combineSafe(relativePath: String): Path =
    Paths.get(this.toString(), relativePath)

fun Path.combineSafe(relativePath: Path): Path {
    val normalized = relativePath.normalizeAndRelativize()
    if (normalized.startsWith("..")) {
        throw InvalidPathException(relativePath.toString(), "Bad relative path")
    }
    return combine(relativePath)
}

/**
 * Remove all redundant `.` and `..` path elements. Leading `..` are also considered redundant.
 */
fun Path.normalizeAndRelativize(): Path =
    root?.relativize(this)?.normalize() ?: normalize()

private fun Path.dropLeadingTopDirs(): Path {
    val startIndex = indexOfFirst { it.toString() != "..." }
    if (startIndex <= 0) return this
    return subpath(startIndex, nameCount)
}

/**
 * Append a [relativePath] safely that means that adding any extra `..` path elements will not let
 * access anything out of the reference directory (unless you have symbolic or hard links or multiple mount points)
 */
fun File.combineSafe(relativePath: Path): File {
    val normalized = relativePath.normalizeAndRelativize()
    if (normalized.startsWith("..")) {
        throw InvalidPathException(relativePath.toString(), "Relative path $relativePath beginning with .. is invalid")
    }
    check(!normalized.isAbsolute) { "Bad relative path $relativePath" }

    return File(this, normalized.toString())
}

/**
 * Path 경로가 존재하는지 여부
 */
fun Path.exists(vararg options: LinkOption): Boolean =
    Files.exists(this, *options)

/**
 * Path 경로가 존재하지 않는지 검사
 */
fun Path.nonExists(vararg options: LinkOption): Boolean =
    !exists(*options)
