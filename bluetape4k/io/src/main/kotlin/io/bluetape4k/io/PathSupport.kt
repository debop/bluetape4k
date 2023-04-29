package io.bluetape4k.io

import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

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

fun Path.normalizeAndRelativize(): Path =
    root?.relativize(this)?.normalize() ?: normalize()

/** Path 경로가 존재하는지 여부 */
fun Path.exists(vararg options: LinkOption): Boolean =
    Files.exists(this, *options)

/** Path 경로가 존재하지 않는지 검사  */
fun Path.nonExists(vararg options: LinkOption): Boolean =
    !exists(*options)
