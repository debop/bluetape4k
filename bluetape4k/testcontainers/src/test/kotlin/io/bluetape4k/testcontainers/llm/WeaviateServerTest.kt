package io.bluetape4k.testcontainers.llm

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.weaviate.client.base.Result
import io.weaviate.client.v1.misc.model.Meta
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class WeaviateServerTest {

    companion object: KLogging()

    @Test
    fun `launch weaviate server`() {
        WeaviateServer().use { weaviate ->
            weaviate.start()
            assertConnectToWeaviate(weaviate)
        }
    }

    @Test
    fun `launch weaviate server with default port`() {
        WeaviateServer(useDefaultPort = true).use { weaviate ->
            weaviate.start()
            assertConnectToWeaviate(weaviate)
        }
    }


    @Test
    fun `weaviate with modules`() {
        val enableModules = listOf(
            "backup-filesystem",
            "text2vec-openai",
            "text2vec-cohere",
            "text2vec-huggingface",
            "generative-openai"
        )

        WeaviateServer().use { weaviate ->
            weaviate.withModules(enableModules)
            weaviate.withBackupFileSystemPath("/tmp/backup")

            weaviate.start()

            val meta = assertConnectToWeaviate(weaviate)
            val modules = meta.result.modules
            log.debug { "modules: $modules" }
            modules.shouldNotBeNull()
            modules shouldBeInstanceOf Map::class
            if (modules is Map<*, *>) {
                val keys = modules.keys
                keys shouldHaveSize enableModules.size
                enableModules shouldContainSame keys
            }
        }
    }

    private fun assertConnectToWeaviate(weaviate: WeaviateServer): Result<Meta> {
        val client = WeaviateServer.Launcher.createClient(weaviate)
        val meta = client.misc().metaGetter().run()

        log.debug { "meta: $meta" }
        meta.result.version shouldBeEqualTo WeaviateServer.TAG
        return meta
    }
}
