plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    // Kafka
    api(Libs.kafka_clients)
    compileOnly(Libs.kafka_metadata)
    compileOnly(Libs.kafka_streams)
    api(Libs.spring_kafka)
    testImplementation(Libs.spring_kafka_test)
    compileOnly(Libs.springData("commons"))

    api(project(":bluetape4k-infra-kafka"))
    api(project(":bluetape4k-io-json"))
    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers_kafka)

    api(project(":bluetape4k-spring-support"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    compileOnly(Libs.reactor_kafka)
    compileOnly(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}
