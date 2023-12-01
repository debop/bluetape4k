plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(Libs.logback)

    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Kafka
    api(Libs.kafka_clients)
    testImplementation(Libs.testcontainers_kafka)

    compileOnly(project(":bluetape4k-core"))

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

}
