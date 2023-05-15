package io.bluetape4k.testcontainers.storage

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.GenericServer
import io.bluetape4k.testcontainers.exposeCustomPorts
import io.bluetape4k.testcontainers.writeToSystemProperties
import io.bluetape4k.utils.ShutdownQueue
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

class MongoDBServer private constructor(
    imageName: DockerImageName,
    useDefaultPort: Boolean,
    reuse: Boolean,
    private val databaseName: String,
): MongoDBContainer(imageName), GenericServer {

    companion object: KLogging() {
        const val IMAGE = "mongo"
        const val TAG = "6"
        const val NAME = "mongo"
        const val PORT = 27017
        const val DATABASE_NAME = "test"

        @JvmStatic
        operator fun invoke(
            tag: String = TAG,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
            databaseName: String = DATABASE_NAME,
        ): MongoDBServer {
            val imageName = DockerImageName.parse(IMAGE).withTag(tag)
            return MongoDBServer(imageName, useDefaultPort, reuse, databaseName)
        }

        @JvmStatic
        operator fun invoke(
            imageName: DockerImageName,
            useDefaultPort: Boolean = false,
            reuse: Boolean = true,
            databaseName: String = DATABASE_NAME,
        ): MongoDBServer {
            return MongoDBServer(imageName, useDefaultPort, reuse, databaseName)
        }
    }

    override val port: Int get() = getMappedPort(PORT)

    /**
     * Url (예: mongodb://localhost:27017/test)
     */
    override val url: String get() = this.getReplicaSetUrl(databaseName)

    init {
        addExposedPorts(PORT)
        withReuse(reuse)

        if (useDefaultPort) {
            exposeCustomPorts(PORT)
        }
    }

    override fun start() {
        super.start()
        writeToSystemProperties(IMAGE)
    }

    object Launcher {
        val mongoDB: MongoDBServer by lazy {
            MongoDBServer().apply {
                start()
                // JVM 종료 시, 자동으로 Close 되도록 합니다
                ShutdownQueue.register(this)
            }
        }

        fun getClient(connectionString: String = mongoDB.url): MongoClient {
            return MongoClients.create(connectionString)
        }
    }
}
