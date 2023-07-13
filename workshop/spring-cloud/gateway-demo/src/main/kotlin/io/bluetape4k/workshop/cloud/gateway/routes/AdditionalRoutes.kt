package io.bluetape4k.workshop.cloud.gateway.routes

import io.bluetape4k.logging.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class AdditionalRoutes {

    companion object: KLogging()

    @Value("\${test.uri:https://httpbin.org:80}")
    var uri: String? = null

    @Bean
    fun additionalRouteRocator(builder: RouteLocatorBuilder) = builder.routes {
        route(id = "test-kotlin") {
            host("kotlin.abc.org") and path("/anything/kotlinroute")
            filters {
                prefixPath("/httpbin")
                addResponseHeader("X-TestHeader", "foobar")
            }
            uri(uri)
        }
    }
}
