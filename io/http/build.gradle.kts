configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-io-netty"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-infra-resilience4j"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_reactive)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // OkHttp3
    compileOnly(Libs.okhttp3)
    compileOnly(Libs.okhttp3_logging_interceptor)

    // OkHttp3 MockWebServer
    compileOnly(Libs.okhttp3_mockwebserver)

    // Apache HttpCompoents HttpClient 5
    compileOnly(Libs.httpclient5)
    compileOnly(Libs.httpclient5_cache)
    compileOnly(Libs.httpclient5_fluent)
    compileOnly(Libs.httpcore5)
    compileOnly(Libs.httpcore5_h2)
    compileOnly(Libs.httpcore5_reactive)

    // Vertx
    compileOnly(project(":bluetape4k-vertx-core"))
    compileOnly(Libs.vertx_core)
    compileOnly(Libs.vertx_lang_kotlin)
    compileOnly(Libs.vertx_lang_kotlin_coroutines)

    // Apache AsyncHttpClient
    compileOnly(Libs.async_http_client)

    // Jackson
    compileOnly(project(":bluetape4k-io-json"))
    compileOnly(Libs.jackson_databind)
    compileOnly(Libs.jackson_module_kotlin)

    // Gson
    compileOnly(Libs.gson)
    compileOnly(Libs.gson_javatime_serializers)
}
