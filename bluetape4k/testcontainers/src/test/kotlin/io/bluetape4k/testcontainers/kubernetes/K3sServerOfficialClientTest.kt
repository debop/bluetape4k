package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import java.io.StringReader
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class K3sServerOfficialClientTest {

    companion object: KLogging()

    private val k3s: K3sServer by lazy { K3sServer.Launcher.k3s }

    @Test
    fun ` k3s should start and list node`() {
        val kubeConfigYml = k3s.kubeConfigYaml

        val client = Config.fromConfig(StringReader(kubeConfigYml))
        val api = CoreV1Api(client)

        val nodes = api.listNode(
            null, null, null, null,
            null, null, null, null,
            null, null
        )
        nodes.items.forEach {
            log.debug { "node=$it" }
        }
        nodes.items shouldHaveSize 1
    }
}
