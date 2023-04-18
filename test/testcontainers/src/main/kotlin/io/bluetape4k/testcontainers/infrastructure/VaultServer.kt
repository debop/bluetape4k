package io.bluetape4k.testcontainers.infrastructure

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.vault.VaultContainer

/**
 * VaultServer
 *
 * 참고: [Vault docker image](https://hub.docker.com/_/vault)
 */
class VaultServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean = false,
    reuse: Boolean = true,
): VaultContainer<VaultServer>(imageName), GenericServer {

    companion object: KLogging() {
        const val TAG = "1.13.1"
        const val NAME = "vault"
        const val PORT = 8200

        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): VaultServer {
            return VaultServer(imageName, useDefaultPort, reuse)
        }

        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
        ): VaultServer {
            val imageName = DockerImageName.parse(NAME).withTag(tag)
            return VaultServer(imageName, useDefaultPort, reuse)
        }
    }

    override val url: String
        get() = "http://$host:$port"

    override val port: Int
        get() = getMappedPort(PORT)

    init {
        withReuse(reuse)
        withLogConsumer(Slf4jLogConsumer(log))

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()

        val extraProps = mapOf("token" to envMap["VAULT_TOKEN"])
        writeToSystemProperties(NAME, extraProps)
    }

    fun createVaultClient(token: String): Vault {
        val config = VaultConfig()
            .address(url)
            .engineVersion(2)
            .token(token)
            .build()
        return Vault(config)
    }
}
