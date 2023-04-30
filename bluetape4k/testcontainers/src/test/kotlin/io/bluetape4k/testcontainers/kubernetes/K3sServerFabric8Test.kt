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
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import java.util.concurrent.TimeUnit
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Disabled("Apple Silicon 에서는 K3s를 사용할 수 없다")
@Execution(ExecutionMode.SAME_THREAD)
class K3sServerFabric8Test {

    companion object: KLogging()

    private val k3s: K3sServer by lazy { K3sServer.Launcher.k3s }

    @Test
    fun `k3s start and have listable node`() {

        // connecting_with_fabric8 {
        // obtain a kubeconfig file which allows us to connect to k3s
        val kubeConfigYaml = k3s.kubeConfigYaml
        log.debug { "kubeConfigYaml:\n$kubeConfigYaml" }

        // requires io.fabric8:kubernetes-client:5.11.0 or higher
        val config = Config.fromKubeconfig(kubeConfigYaml)
        val client = DefaultKubernetesClient(config)

        // interact with the running K3s server, e.g.:
        val nodes = client.nodes().list().items

        nodes shouldHaveSize 1

        // verify that we can start a pod
        val helloworld = dummyStartablePod()
        client.pods().create(helloworld)
        client.pods()
            .inNamespace("default")
            .withName("helloworld")
            .waitUntilReady(30, TimeUnit.SECONDS)

        client.pods().inNamespace("default").withName("helloworld").isReady.shouldBeTrue()
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
