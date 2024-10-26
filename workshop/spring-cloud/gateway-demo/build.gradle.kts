plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.cloud.gateway.GatewayApplicationKt")
}

dependencyManagement {
    imports {
        mavenBom(Libs.micrometer_bom)
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-json"))
    implementation(project(":bluetape4k-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Bucket4j
    api(Libs.bucket4j_core)
    api(Libs.bucket4j_redis)
    api(Libs.bucket4j_spring_boot)

    // Redis
    api(project(":bluetape4k-redis"))
    api(Libs.lettuce_core)
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers)

    api(Libs.jakarta_servlet_api)

    // Micrometer
    implementation(project(":bluetape4k-micrometer"))
    implementation(Libs.micrometer_registry_prometheus)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springCloudStarter("gateway"))
    testImplementation(Libs.springCloudStarter("loadbalancer"))
    testImplementation(Libs.springCloud("test-support"))
    testImplementation(Libs.springCloud("gateway-server") + "::tests")

    implementation(Libs.springBootStarter("webflux"))
    // implementation(Libs.springBootStarter("cache"))
    // implementation(Libs.springBootStarter("validation"))
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.micrometer_core)
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.reactor_netty)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
