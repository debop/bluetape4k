package io.bluetape4k.testcontainers.llm

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.chromadb.ChromaDBContainer
import org.testcontainers.utility.DockerImageName

class ChromaDBServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): ChromaDBContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "chromadb/chroma"
        const val TAG = "latest"
        const val NAME = "chromadb"
        const val PORT = 8000

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ChromaDBServer {
            return ChromaDBServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): ChromaDBServer {
            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }
    }

    override val port: Int get() = firstMappedPort
    override val url: String get() = endpoint

    init {
        withExposedPorts(PORT)
        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(NAME)
    }

    object Launcher {
        val chromaDBServer: ChromaDBServer by lazy {
            ChromaDBServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }

}
