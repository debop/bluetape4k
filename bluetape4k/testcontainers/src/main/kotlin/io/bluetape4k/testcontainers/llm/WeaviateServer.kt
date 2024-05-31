package io.bluetape4k.testcontainers.llm

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import org.testcontainers.utility.DockerImageName
import org.testcontainers.weaviate.WeaviateContainer

/**
 * Testcontainers implementation of Weaviate.
 * <p>
 * Supported images: `cr.weaviate.io/semitechnologies/weaviate`, `semitechnologies/weaviate`
 * <p>
 * Exposed ports:
 * <ul>
 *     <li>HTTP: 8080</li>
 *     <li>gRPC: 50051</li>
 * </ul>
 */
class WeaviateServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
    env: Map<String, String>,
): WeaviateContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "semitechnologies/weaviate"
        const val TAG = "1.24.6"
        const val NAME = "weaviate"
        const val HTTP_PORT = 8080
        const val GRPC_PORT = 50051

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
            env: Map<String, String> = emptyMap(),
        ): WeaviateServer {
            return WeaviateServer(imageName, useDefaultPort, reuse, env)
        }

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
            env: Map<String, String> = emptyMap(),
        ): WeaviateServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse, env)
        }
    }

    override val port: Int get() = getMappedPort(HTTP_PORT)
    override val url: String get() = "$host:$port"

    val httpPort: Int get() = getMappedPort(HTTP_PORT)
    val grpcPort: Int get() = getMappedPort(GRPC_PORT)

    val httpUrl: String get() = "$host:$httpPort"
    val grpcUrl: String get() = "$host:$grpcPort"

    init {
        withExposedPorts(HTTP_PORT, GRPC_PORT)
        withReuse(reuse)

        if (env.isNotEmpty()) {
            withEnv(env)
        }

        if (useDefaultPort) {
            exposeCustomPorts(HTTP_PORT, GRPC_PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf(
            "http.port" to httpPort,
            "grpc.port" to grpcPort
        )
        writeToSystemProperties(NAME, extraProps)
    }

    fun withModules(vararg enableModules: String) {
        withEnv("ENABLE_MODULES", enableModules.joinToString(","))
    }

    fun withModules(enableModules: Collection<String>) {
        withEnv("ENABLE_MODULES", enableModules.joinToString(","))
    }

    fun withBackupFileSystemPath(path: String = "/tmp/backup") {
        withEnv("BACKUP_FILESYSTEM_PATH", path)
    }

    object Launcher {
        val weaviate: WeaviateServer by lazy {
            WeaviateServer()
                .apply {
                    start()
                    ShutdownQueue.register(this)
                }
        }

        fun createClient(weaviate: WeaviateServer): WeaviateClient {
            val config = Config("http", weaviate.httpUrl)
            config.grpcHost = weaviate.grpcUrl

            return WeaviateClient(config)
        }
    }
}
