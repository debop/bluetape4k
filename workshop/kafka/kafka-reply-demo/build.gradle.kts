plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.kafka.KafkaApplicationKt")
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
    compileOnly(Libs.spring_kafka_test)
    compileOnly(Libs.springData("commons"))

    api(project(":bluetape4k-kafka"))
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers_kafka)

    // Jackson
    api(project(":bluetape4k-json"))
    api(Libs.jackson_databind)
    api(Libs.jackson_module_kotlin)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    compileOnly(Libs.reactor_kafka)
    compileOnly(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)

    api(project(":bluetape4k-spring-support"))
    testImplementation(project(":bluetape4k-junit5"))

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("webflux"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}
