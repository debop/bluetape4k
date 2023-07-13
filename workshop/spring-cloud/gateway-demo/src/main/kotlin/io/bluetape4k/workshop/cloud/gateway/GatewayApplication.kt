package io.bluetape4k.workshop.cloud.gateway

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.cloud.gateway.routes.AdditionalRoutes
import io.bluetape4k.workshop.cloud.gateway.routes.HttpbinRoutes
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(HttpbinRoutes::class, AdditionalRoutes::class)
class GatewayApplication {

    companion object: KLogging() {
        // TODO: 우선 Local Bucket4j 를 사용한다. 최종적으로는 Redis 를 이용한 분산 Bucket4j 를 사용하도록 한다
        // val redis = RedisServer.Launcher.redis
    }

}

fun main(vararg args: String) {
    runApplication<GatewayApplication>(*args) {
        // webApplicationType = WebApplicationType.REACTIVE
    }
}
