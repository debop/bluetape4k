plugins {
    kotlin("plugin.spring")
}

configurations {
    // compileOnly 나 runtimeOnly로 지정된 Dependency를 testImplementation 으로도 지정하도록 합니다.
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-io"))
    testImplementation(project(":bluetape4k-junit5"))

    // Micrometer
    api(Libs.micrometer_core)
    compileOnly(Libs.micrometer_registry_prometheus)
    testImplementation(Libs.micrometer_test)

    // Micrometer Tracing
    compileOnly(Libs.micrometer_tracing_bridge_otel)
    testImplementation(Libs.micrometer_tracing_test)
    testImplementation(Libs.micrometer_tracing_integeration_test)

    // Instrumentations
    compileOnly(Libs.cache2k_micrometer)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_slf4j)
    testImplementation(Libs.kotlinx_coroutines_test)
}
