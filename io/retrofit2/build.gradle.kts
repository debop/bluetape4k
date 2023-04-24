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

    // Retrofit2
    api(Libs.retrofit2)
    api(Libs.retrofit2_converter_jackson)
    api(Libs.retrofit2_adapter_java8)
    compileOnly(Libs.retrofit2_adapter_reactor)
    compileOnly(Libs.retrofit2_adapter_rxjava2)
    compileOnly(Libs.retrofit2_adapter_rxjava3)
    testImplementation(Libs.retrofit2_mock)

    // OkHttp3
    api(Libs.okhttp3)
    api(Libs.okhttp3_logging_interceptor)

    // OkHttp3 MockWebServer
    testImplementation(Libs.okhttp3_mockwebserver)

    // Apache HttpCompoents HttpClient 5
    api(Libs.httpclient5)

    // Vertx
    compileOnly(project(":bluetape4k-vertx-core"))
    compileOnly(Libs.vertx_core)
    compileOnly(Libs.vertx_lang_kotlin)
    compileOnly(Libs.vertx_lang_kotlin_coroutines)

    // Apache AsyncHttpClient
    compileOnly(Libs.async_http_client)
    compileOnly(Libs.async_http_client_extras_retrofit2)
    compileOnly(Libs.async_http_client_extras_rxjava2)

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
}
