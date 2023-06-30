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
    api(project(":bluetape4k-openai-api"))

    api(project(":bluetape4k-io-http"))
    api(project(":bluetape4k-io-netty"))
    api(project(":bluetape4k-infra-resilience4j"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_reactive)
    api(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Retrofit2
    api(project(":bluetape4k-io-retrofit2"))
    compileOnly(project(":bluetape4k-spring-retrofit2"))

    // Retrofit2
    api(Libs.retrofit2)
    api(Libs.retrofit2_converter_jackson)
    api(Libs.retrofit2_converter_scalars)
    api(Libs.retrofit2_adapter_java8)
    api(Libs.retrofit2_adapter_reactor)
    compileOnly(Libs.retrofit2_adapter_rxjava2)
    compileOnly(Libs.retrofit2_adapter_rxjava3)
    testImplementation(Libs.retrofit2_mock)

    // OkHttp3
    api(Libs.okhttp3)
    api(Libs.okhttp3_logging_interceptor)

    // OkHttp3 MockWebServer
    testImplementation(Libs.okhttp3_mockwebserver)

    // Apache HttpCompoents HttpClient 5
    // feign_hc5 를 사용하려면, httpcore5, httpcore5-h2 도 버전을 맞춰줘야 한다 
    api(Libs.httpclient5)
    api(Libs.httpcore5)
    api(Libs.httpcore5_h2)

    // Vertx
    compileOnly(project(":bluetape4k-vertx-core"))
    compileOnly(Libs.vertx_core)
    compileOnly(Libs.vertx_lang_kotlin)
    compileOnly(Libs.vertx_lang_kotlin_coroutines)

    // Jackson (2.14 와 2.13 이 혼용되어서 jackson-core, jackson-databind 를 모두 지정해주어야 한다)
    api(project(":bluetape4k-io-json"))
    api(Libs.jackson_core)
    api(Libs.jackson_databind)
    api(Libs.jackson_module_kotlin)
    api(Libs.jackson_module_jsonSchema)

    // Gson
    compileOnly(Libs.gson)
    compileOnly(Libs.gson_javatime_serializers)

    // Resilience4j
    compileOnly(project(":bluetape4k-infra-resilience4j"))
    compileOnly(Libs.resilience4j_all)
    compileOnly(Libs.resilience4j_kotlin)
    compileOnly(Libs.resilience4j_feign)
    compileOnly(Libs.resilience4j_cache)
    compileOnly(Libs.resilience4j_retry)
    compileOnly(Libs.resilience4j_circuitbreaker)
    compileOnly(Libs.resilience4j_reactor)

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito")
    }
}
