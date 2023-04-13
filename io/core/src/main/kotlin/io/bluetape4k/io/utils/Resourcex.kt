package io.bluetape4k.io.utils

import io.bluetape4k.core.assertNotBlank
import io.bluetape4k.core.support.EMPTY_STRING
import io.bluetape4k.core.support.emptyByteArray
import io.bluetape4k.io.toByteArray
import io.bluetape4k.io.toString
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Resource 관련 Helper object
 */
object Resourcex : KLogging() {

    private val currentClassLoader: ClassLoader = Resourcex::class.java.classLoader

    /**
     * 리소스 파일을 읽어드리는 [InputStream]을 반환합니다.
     *
     * ```
     * val is:InputStream = Resourcex.getString("files/Utf8Samples.txt")
     * ```
     *
     * @param path         리소스 파일의 경로
     * @param charset      문자열의 charset (기본: UTF-8)
     * @param classLoader  Class Loader
     * @return [InputStream] 인스턴스, 파일이 없으면 null 을 반환
     */
    @JvmStatic
    @JvmOverloads
    fun getInputStream(path: String, classLoader: ClassLoader = currentClassLoader): InputStream? {
        path.assertNotBlank("path")
        log.debug { "Load resource file... path=[$path]" }

        val url = path.removePrefix("/")
        return classLoader.getResourceAsStream(url).apply {
            if (this == null) {
                log.warn { "Resource not found. path=[$path]" }
            }
        }
    }

    /**
     * 리소스 파일을 읽어 문자열로 반환합니다.
     *
     * ```
     * val text:String = Resourcex.getString("files/Utf8Samples.txt")
     * ```
     *
     * @param path         리소스 파일의 경로
     * @param charset      문자열의 charset (기본: UTF-8)
     * @param classLoader  Class Loader
     * @return 파일 내용의 문자열, 파일이 없으면 빈 문자열을 반환
     */
    @JvmStatic
    @JvmOverloads
    fun getString(
        path: String,
        charset: Charset = Charsets.UTF_8,
        classLoader: ClassLoader = currentClassLoader,
    ): String {
        return getInputStream(path, classLoader)?.use { it.toString(charset) } ?: EMPTY_STRING
    }

    /**
     * 리소스 파일을 읽어 문자열로 반환합니다.
     *
     * ```
     * val bytes:ByteArray = Resourcex.getBytes("files/Utf8Samples.txt")
     * ```
     *
     * @param path         리소스 파일의 경로
     * @param classLoader  Class Loader
     * @return 파일 내용을 가진 [ByteArray], 파일이 없으면 null 을 반환
     */
    @JvmStatic
    @JvmOverloads
    fun getBytes(path: String, classLoader: ClassLoader = currentClassLoader): ByteArray {
        return getInputStream(path, classLoader)?.use { it.toByteArray() } ?: emptyByteArray
    }
}
