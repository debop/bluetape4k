package io.bluetape4k.testcontainers.kubernetes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.io.StringReader

@Execution(ExecutionMode.SAME_THREAD)
class K3sServerOfficialClientTest: AbstractK3sTest() {

    companion object: KLogging()

    @Test
    fun ` k3s should start and list node`() {
        val kubeConfigYml = k3s.kubeConfigYaml

        val client = Config.fromConfig(StringReader(kubeConfigYml))
        val api = CoreV1Api(client)

        val nodes = api.listNode().execute()
        nodes.items.forEach {
            log.debug { "node=$it" }
        }
        nodes.items shouldHaveSize 1
    }
}
