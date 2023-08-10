package io.bluetape4k.utils

import io.bluetape4k.support.unsafeLazy
import java.util.*

/**
 * System Property 를 제공해주는 Object
 */
object Systemx {

    const val USER_DIR = "user.dir"
    const val USER_NAME = "user.name"
    const val USER_HOME = "user.home"
    const val JAVA_HOME = "java.home"
    const val TEMP_DIR = "java.io.tmpdir"
    const val OS_NAME = "os.name"
    const val OS_VERSION = "os.version"
    const val JAVA_VERSION = "java.version"
    const val JAVA_CLASS_VERION = "java.class.version"
    const val JAVA_SPECIFICATION_VERSION = "java.specification.version"
    const val JAVA_VENDOR = "java.vendor"
    const val JAVA_CLASSPATH = "java.class.path"
    const val PATH_SEPARATOR = "path.separator"
    const val HTTP_PROXY_HOST = "http.proxyHost"
    const val HTTP_PROXY_PORT = "http.proxyPort"
    const val HTTP_PROXY_USER = "http.proxyUser"
    const val HTTP_PROXY_PASSWORD = "http.proxyPassword"
    const val FILE_ENCODING = "file.encoding"
    const val SUN_BOOT_CLASS_PATH = "sun.boot.class.path"

    /** Runtime package */
    @JvmStatic
    val RuntimePackage: Package by unsafeLazy { Runtime::class.java.`package` }

    /** System Properties */
    @JvmStatic
    val SystemProps: Properties by unsafeLazy { System.getProperties() }

    /** CPU Core count */
    @JvmStatic
    val ProcessCount: Int by unsafeLazy { Runtime.getRuntime().availableProcessors() }

    @JvmStatic
    val JavaCompiler: String? by unsafeLazy { System.getProperty("java.compiler") }

    /** JVM 버전 */
    @JvmStatic
    val JavaVersion: String? by unsafeLazy { RuntimePackage.specificationVersion }

    /** JVM 구현 버전 */
    @JvmStatic
    val JavaImplementationVersion: String? by unsafeLazy { RuntimePackage.implementationVersion }

    /** JVM 벤더 */
    @JvmStatic
    val JavaVendor: String? by unsafeLazy { RuntimePackage.specificationVendor }

    @JvmStatic
    val JavaVendorUrl: String? by unsafeLazy { System.getProperty("java.vendor.url") }

    /** JVM 구현 벤더  */
    @JvmStatic
    val JavaImplementationVendor: String? by unsafeLazy { RuntimePackage.implementationVendor }

    @JvmStatic
    val JavaClassVersion: String? by unsafeLazy { System.getProperty(JAVA_CLASS_VERION) }

    @JvmStatic
    val JavaLibraryPath: String? by unsafeLazy { System.getProperty("java.library.path") }

    @JvmStatic
    val JavaRuntimeName: String? by unsafeLazy { System.getProperty("java.runtime.name") }

    @JvmStatic
    val JavaRuntimeVersion: String? by unsafeLazy { System.getProperty("java.runtime.version") }

    @JvmStatic
    val JavaSpecificationName: String? by unsafeLazy { System.getProperty("java.specification.name") }

    @JvmStatic
    val JavaSpecificationVendor: String? by unsafeLazy { System.getProperty("java.specification.vendor") }

    @JvmStatic
    val IsJava6: Boolean by unsafeLazy { JavaVersion == "1.6" }

    @JvmStatic
    val IsJava7: Boolean by unsafeLazy { JavaVersion == "1.7" }

    @JvmStatic
    val IsJava8: Boolean by unsafeLazy { JavaVersion == "1.8" }

    @JvmStatic
    val IsJava9: Boolean by unsafeLazy { JavaVersion == "1.9" }

    /** JVM home directory */
    @JvmStatic
    val JavaHome: String? by unsafeLazy { System.getProperty("java.home") }

    @JvmStatic
    val LineSeparator: String by unsafeLazy { System.getProperty("line.separator") }

    @JvmStatic
    val FileSeparator: String by unsafeLazy { System.getProperty("file.separator") }

    @JvmStatic
    val PathSeparator: String by unsafeLazy { System.getProperty("path.separator") }

    @JvmStatic
    val FileEncoding: String by unsafeLazy { System.getProperty("file.encoding") ?: Charsets.UTF_8.name() }

    @JvmStatic
    val UserName: String? by unsafeLazy { System.getProperty(USER_NAME) }

    @JvmStatic
    val UserHome: String? by unsafeLazy { System.getProperty(USER_HOME) }

    @JvmStatic
    val UserDir: String? by unsafeLazy { System.getProperty(USER_DIR) }

    @JvmStatic
    val UserCountry: String? by unsafeLazy { System.getProperty("user.country") ?: System.getProperty("user.region") }

    @JvmStatic
    val TempDir: String? by unsafeLazy { System.getProperty(TEMP_DIR) }

    @JvmStatic
    val JavaIOTmpDir: String? by unsafeLazy { System.getProperty(TEMP_DIR) }

    @JvmStatic
    val OSName: String? by unsafeLazy { System.getProperty(OS_NAME) }

    @JvmStatic
    val OSVersion: String? by unsafeLazy { System.getProperty(OS_VERSION) }

    @JvmStatic
    val isWindows: Boolean by unsafeLazy { OSName?.contains("win") ?: false }

    @JvmStatic
    val isMac: Boolean by unsafeLazy { OSName?.contains("mac") ?: false }

    @JvmStatic
    val isSolaris: Boolean by unsafeLazy { OSName?.contains("sunos") ?: false }

    @JvmStatic
    val isUnix: Boolean by unsafeLazy {
        OSName?.contains("nix") ?: false ||
            OSName?.contains("nux") ?: false ||
            OSName?.contains("aix") ?: false
    }

}
