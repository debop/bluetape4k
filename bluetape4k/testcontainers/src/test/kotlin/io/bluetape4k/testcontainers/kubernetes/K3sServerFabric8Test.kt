package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.fabric8.kubernetes.api.model.ContainerBuilder
import io.fabric8.kubernetes.api.model.ContainerPortBuilder
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodBuilder
import io.fabric8.kubernetes.api.model.PodSpecBuilder
import io.fabric8.kubernetes.api.model.ProbeBuilder
import io.fabric8.kubernetes.client.Config
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.concurrent.TimeUnit

@Execution(ExecutionMode.SAME_THREAD)
class K3sServerFabric8Test: AbstractK3sTest() {

    companion object: KLogging()

    @Test
    fun `k3s start and have listable node`() {

        // connecting_with_fabric8 {
        // obtain a kubeconfig file which allows us to connect to k3s
        val kubeConfigYaml = k3s.kubeConfigYaml
        log.debug { "kubeConfigYaml:\n$kubeConfigYaml" }

        // requires io.fabric8:kubernetes-client:5.11.0 or higher
        val config = Config.fromKubeconfig(kubeConfigYaml)
        val client = KubernetesClientBuilder().withConfig(config).build()

        // interact with the running K3s server, e.g.:
        val nodes = client.nodes().list().items

        nodes shouldHaveSize 1

        // verify that we can start a pod
        val helloworld = dummyStartablePod()
        client.pods().resource(helloworld).create()
        client.pods()
            .inNamespace("default")
            .withName("helloworld")
            .waitUntilReady(30, TimeUnit.SECONDS)

        client.pods().inNamespace("default").withName("helloworld").isReady.shouldBeTrue()

        client.close()
    }

    private fun dummyStartablePod(): Pod {
        val container = ContainerBuilder()
            .withName("helloworld")
            .withImage("testcontainers/helloworld:1.1.0")
            .withPorts(ContainerPortBuilder().withContainerPort(8080).build())
            .withReadinessProbe(ProbeBuilder().withNewTcpSocket().withNewPort(8080).endTcpSocket().build())
            .build()

        val podSpec = PodSpecBuilder().withContainers(container).build()

        return PodBuilder()
            .withNewMetadata()
            .withName("helloworld")
            .withNamespace("default")
            .endMetadata()
            .withSpec(podSpec)
            .build()
    }
}
