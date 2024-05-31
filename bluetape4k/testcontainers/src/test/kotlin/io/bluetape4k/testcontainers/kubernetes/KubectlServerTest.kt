package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import org.testcontainers.containers.Network
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy
import org.testcontainers.images.builder.Transferable
import java.time.Duration

class KubectlServerTest: AbstractK3sTest() {

    companion object: KLogging()

    @Test
    fun `network alias를 통해 kube config를 노출시키기`() {
        val network = Network.SHARED

        K3sServer().use { k3s ->
            k3s.withNetwork(network).withNetworkAliases("k3s")
            k3s.start()

            val configYaml = k3s.generateInternalKubeConfigYaml("k3s")

            // k3s에 대한 kube config를 kubectl server에 노출시키기
            KubectlServer()
                .withNetwork(network)
                .withCopyToContainer(Transferable.of(configYaml), "/.kube/config")
                .withCommand("get namespaces")
                .withStartupCheckStrategy(OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(30)))
                .use { kube ->
                    kube.start()
                    kube.logs shouldContain "kube-system"
                }
        }
    }
}
