plugins {
    kotlin("plugin.spring")
}

configurations {
    // compileOnly 나 runtimeOnly로 지정된 Dependency를 testImplementation 으로도 지정하도록 합니다.
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-infra-cache"))
    testImplementation(project(":bluetape4k-io-http"))
    testImplementation(project(":bluetape4k-io-json"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Micrometer
    api(Libs.micrometer_core)
    compileOnly(Libs.micrometer_registry_prometheus)
    testImplementation(Libs.micrometer_test)

    api(Libs.commons_lang3)

    // Micrometer Tracing
    compileOnly(Libs.micrometer_tracing_bridge_otel)
    testImplementation(Libs.micrometer_tracing_test)
    testImplementation(Libs.micrometer_tracing_integeration_test)

    // Instrumentations
    compileOnly(Libs.cache2k_core)
    // 이미 cache2k_micrometer에 instrument 가 있지만, 예제용으로 만들기 위해 직접 구현했습니다.
    // compileOnly(Libs.cache2k_micrometer)
    compileOnly(Libs.ignite_core)

    // Retrofit2 Instrumentations
    testImplementation(project(":bluetape4k-io-retrofit2"))
    compileOnly(Libs.retrofit2)
    compileOnly(Libs.retrofit2_adapter_reactor)
    compileOnly(Libs.retrofit2_adapter_rxjava2)
    compileOnly(Libs.retrofit2_adapter_rxjava3)
    compileOnly(Libs.retrofit2_converter_jackson)
    compileOnly(Libs.okhttp3)

    compileOnly(Libs.async_http_client_extras_retrofit2)

    compileOnly(Libs.httpcore5)
    compileOnly(Libs.vertx_core)
    testImplementation(project(":bluetape4k-vertx-core"))

    // Coroutines
    compileOnly(project(":bluetape4k-coroutines"))
    testImplementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)
}
