package io.bluetape4k.workshop.cloud.gateway.routes

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
class HttpbinRoutes {

    companion object: KLogging() {
        const val HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS =
            "hello from fake /actuator/metrics/spring.cloud.gateway.requests"

        const val TEST_HEADER = "X-TestHeader"
    }

    @Value("\${test.uri:https://httpbin.org:80}")
    var uri: String? = null

    @Bean
    fun httpbinRouteRouter(builder: RouteLocatorBuilder) = builder.routes {
        route {
            host("**.abc.org") and path("/anything/png")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "foobar")
            }
            uri(uri)
        }
        route(id = "read_body_pred") {
            host("*.readbody.org") and
                    readBody(String::class.java) { it.trim().equals("hi", true) }

            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "read_body_pred")
            }
            uri(uri)
        }

        route("rewrite_request_obj") {
            host("*.rewriterequestobj.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_request")
                modifyRequestBody(
                    String::class.java,
                    Hello::class.java,
                    MediaType.APPLICATION_JSON_VALUE
                ) { exchange: ServerWebExchange, s: String ->
                    Mono.just(Hello(s.uppercase()))
                }
            }
            uri(uri)
        }

        route("rewrite_request_upper") {
            host("*.rewriterequestupper.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_request_upper")
                modifyRequestBody(String::class.java, String::class.java) { exchange, s ->
                    Mono.just(s.uppercase() + s.uppercase())
                }
            }
            uri(uri)
        }

        route("rewrite_response_upper") {
            host("*.rewriteresponseupper.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_response_upper")
                modifyResponseBody(String::class.java, String::class.java) { exchange, s ->
                    log.debug { "response original: $s" }
                    Mono.just(s.uppercase())
                }
            }
            uri(uri)
        }

        route("rewrite_empty_response") {
            host("*.rewriteemptyresponse.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_empty_response")
                modifyResponseBody(String::class.java, String::class.java) { exchange, s ->
                    if (s == null) {
                        Mono.just("emptybody")
                    } else {
                        Mono.just(s.uppercase())
                    }
                }
            }
            uri(uri)
        }

        route("rewrite_response_fail_supplier") {
            host("*.rewriteresponsewithfailsupplier.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_respnose_fail_supplier")
                modifyResponseBody(String::class.java, String::class.java) { exchange, s ->
                    if (s == null) {
                        Mono.error(IllegalArgumentException("this should not happen"))
                    } else {
                        Mono.just(s.uppercase())
                    }
                }
            }
            uri(uri)
        }

        route("rewrite_response_obj") {
            host("*.rewriteresponseobj.org")
            filters {
                prefixPath("/httpbin")
                addResponseHeader(TEST_HEADER, "rewrite_response_obj")
                modifyResponseBody(Map::class.java, String::class.java) { exchange, map ->
                    val data = map["data"]
                    Mono.just(data.toString())
                }
                setResponseHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            }
            uri(uri)
        }

        route {
            path("/image/webp")
            filters {
                prefixPath("/httpbin")
                addResponseHeader("X-AnotherHeader", "baz")
                uri(uri)
            }
        }

        route {
            order(-1)
            host("**.throttle.org") and path("/get")
            filters {
                prefixPath("/httpbin")
                filter(ThrottleGatewayFilter())
            }
            uri(uri)
        }
    }

    @Bean
    fun testFunRouterFunction(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.path("/testfun")) { request ->
            ServerResponse.ok().body(BodyInserters.fromValue("hello"))
        }
    }

    @Bean
    fun testWhenMetricPathIsNotMeet(): RouterFunction<ServerResponse> {
        val predicate = RequestPredicates.path("/actuator/metrics/spring.cloud.gateway.requests")
        return RouterFunctions.route(predicate) { request ->
            ServerResponse.ok().body(BodyInserters.fromValue(HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS))
        }
    }

    data class Hello(var message: String)
}
