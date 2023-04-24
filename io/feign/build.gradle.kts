plugins {
    // Spring 관련 Plugin 은 spring-cloud-openfeign 예제를 위한 것입니다.
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

tasks.bootJar {
    enabled = false
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-io-http"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-io-netty"))
    api(project(":bluetape4k-utils-resilience4j"))
    testImplementation(project(":bluetape4k-test-junit5"))

    // Coroutines
    api(project(":bluetape4k-kotlinx-coroutines"))
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_reactive)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // https://mvnrepository.com/artifact/javax.ws.rs/javax.ws.rs-api
    api(Libs.javax_ws_rs_api)

    // Feign
    api(Libs.feign_hc5)
    api(Libs.feign_kotlin)
    api(Libs.feign_slf4j)
    api(Libs.feign_jackson)
    api(Libs.feign_hc5)
    compileOnly(Libs.feign_jaxrs)
    compileOnly(Libs.feign_jaxrs2)
    compileOnly(Libs.feign_httpclient)
    compileOnly(Libs.feign_java11)
    compileOnly(Libs.feign_okhttp)
    compileOnly(Libs.feign_ribbon)
    compileOnly(Libs.feign_gson)

    // OkHttp3
    compileOnly(Libs.okhttp3)
    compileOnly(Libs.okhttp3_logging_interceptor)

    // OkHttp3 MockWebServer
    testImplementation(Libs.okhttp3_mockwebserver)

    // Apache HttpCompoents HttpClient 5
    api(Libs.httpclient5)

    // Vertx
    compileOnly(project(":bluetape4k-vertx-core"))
    compileOnly(Libs.vertx_core)
    compileOnly(Libs.vertx_lang_kotlin)
    compileOnly(Libs.vertx_lang_kotlin_coroutines)

    // Jackson
    api(project(":bluetape4k-io-json"))
    api(Libs.jackson_databind)
    api(Libs.jackson_module_kotlin)

    // Gson
    compileOnly(Libs.gson)
    compileOnly(Libs.gson_javatime_serializers)

    // Resilience4j
    compileOnly(project(":bluetape4k-utils-resilience4j"))
    compileOnly(Libs.resilience4j_all)
    compileOnly(Libs.resilience4j_kotlin)
    compileOnly(Libs.resilience4j_feign)
    compileOnly(Libs.resilience4j_cache)
    compileOnly(Libs.resilience4j_retry)
    compileOnly(Libs.resilience4j_circuitbreaker)
    compileOnly(Libs.resilience4j_reactor)

    // Netty
    compileOnly(Libs.netty_all)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    compileOnly(Libs.netty_transport_native_epoll + ":linux-x86_64")
    compileOnly(Libs.netty_transport_native_kqueue + ":osx-x86_64")

    //
    // Spring Cloud OpenFeign 사용
    //
    testImplementation(Libs.springCloudStarter("openfeign"))
    testImplementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

}
