plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.resilience4j.ApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-json"))
    implementation(project(":bluetape4k-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Resilience4j
    implementation(project(":bluetape4k-resilience4j"))
    implementation(Libs.resilience4j_spring_boot3)
    implementation(Libs.resilience4j_all)
    implementation(Libs.resilience4j_kotlin)
    implementation(Libs.resilience4j_reactor)
    implementation(Libs.resilience4j_micrometer)

    implementation(Libs.micrometer_registry_prometheus)
    implementation(Libs.micrometer_observation)
    testImplementation(Libs.micrometer_observation_test)

    // Chaos Monkey (https://github.com/codecentric/chaos-monkey-spring-boot)
    implementation(Libs.chaos_monkey_spring_boot)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("webflux"))
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.springBootStarter("aop"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Coroutines
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_jdk8)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.netty_all)
    implementation(Libs.reactor_netty)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
