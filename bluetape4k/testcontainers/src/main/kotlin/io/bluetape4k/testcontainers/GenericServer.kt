package io.bluetape4k.testcontainers

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.info
import org.testcontainers.containers.ContainerState


private val log = KotlinLogging.logger {}

internal const val SERVER_PREFIX = "testcontainers"

/**
 * Docker Container로 실행되는 서버의 기본정보를 표현합니다.
 */
interface GenericServer: ContainerState {

    override fun getHost(): String = super.getHost()

    val port: Int
        get() = firstMappedPort

    val url: String
        get() = "$host:$port"
}

/**
 * 테스트를 위한 Server의 기본 정보를 System Property로 등록하여 Application 환경설정에서 사용할 수 있도록 합니다.
 *
 * ```properties
 * spring.redis.host = ${testcontainers.redis.host}
 * spring.redis.port = ${testcontainers.redis.port}
 * spring.redis.url = ${testcontainers.redis.url}
 * ```
 */
fun <T: GenericServer> T.writeToSystemProperties(name: String, extraProps: Map<String, Any?> = emptyMap()) {
    log.info { "Setup Server properties ..." }

    System.setProperty("$SERVER_PREFIX.$name.host", this.host)
    System.setProperty("$SERVER_PREFIX.$name.port", this.port.toString())
    System.setProperty("$SERVER_PREFIX.$name.url", this.url)

    extraProps.forEach { (key, value) ->
        value?.run {
            System.setProperty("$SERVER_PREFIX.$name.$key", this.toString())
        }
    }

    log.info {
        buildString {
            appendLine()
            appendLine("Start $name Server:")
            appendLine("\t$SERVER_PREFIX.$name.host=$host")
            appendLine("\t$SERVER_PREFIX.$name.port=$port")
            appendLine("\t$SERVER_PREFIX.$name.url=$url")

            extraProps.forEach { (key, value) ->
                value?.run {
                    appendLine("\t$SERVER_PREFIX.$name.$key=$this")
                }
            }
        }
    }
}
