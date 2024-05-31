package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@Suppress("UNUSED_PARAMETER")
class KubectlServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): GenericContainer<KubectlServer>(imageName) {

    companion object: KLogging() {

        const val IMAGE = "rancher/kubectl"
        const val TAG = "v1.23.3"              // https://hub.docker.com/r/rancher/kubectl/tags?page=1
        const val NAME = "kubectl"

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): KubectlServer {
            return KubectlServer(imageName, useDefaultPort, reuse)
        }

        @JvmStatic
        operator fun invoke(
            image: String = IMAGE,
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): KubectlServer {
            image.requireNotBlank("image")
            tag.requireNotBlank("tag")

            val imageName = DockerImageName.parse(image).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }
    }

    init {
        withReuse(reuse)
        withStartupCheckStrategy(OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(30)))
    }

    override fun start() {
        super.start()
    }

    object Launcher {
        val kubectl: KubectlServer by lazy {
            KubectlServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
