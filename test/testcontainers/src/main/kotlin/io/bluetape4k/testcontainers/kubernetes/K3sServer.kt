package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.k3s.K3sContainer
import org.testcontainers.utility.DockerImageName

/**
 * K3s server
 *
 * 참고: [K3s Docker image]
 *
 * @param imageName
 * @param useDefaultPort
 * @param reuse
 */
class K3sServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
): K3sContainer(imageName), GenericServer {

    companion object: KLogging() {

        const val IMAGE = "rancher/k3s"
        const val NAME = "k3s"
        const val TAG = "v1.23.6-k3s1"

        // K3s Ports : https://rancher.com/docs/rancher/v2.5/en/installation/requirements/ports/#commonly-used-ports
        const val KUBE_SECURE_PORT = 6443
        const val RANCHER_WEBHOOK_PORT = 8443

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): K3sServer {
            return K3sServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): K3sServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return invoke(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String get() = "http://$host:$port"
    override val port: Int get() = getMappedPort(KUBE_SECURE_PORT)
    val kubeSecurePort: Int get() = getMappedPort(KUBE_SECURE_PORT)
    val rancherWebhookPort: Int get() = getMappedPort(RANCHER_WEBHOOK_PORT)

    init {
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        if (useDefaultPort) {
            exposeCustomPorts(KUBE_SECURE_PORT, RANCHER_WEBHOOK_PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf(
            "kube.secure.port" to kubeSecurePort,
            "rancher.webhook.port" to rancherWebhookPort
        )
        writeToSystemProperties(NAME, extraProps)
    }

    object Launcher {
        val k3s: K3sServer by lazy {
            K3sServer().apply {
                start()
                ShutdownQueue.register(this)
            }
        }
    }
}
