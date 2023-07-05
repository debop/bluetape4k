plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.problem.ProblemApplicationKt")
    buildInfo()
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-io-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Problem
    implementation(Libs.problem_spring_webflux)
    api(Libs.jakarta_validation_api)

    // Resilience4j
    implementation(project(":bluetape4k-infra-resilience4j"))
    implementation(Libs.resilience4j_all)
    implementation(Libs.resilience4j_kotlin)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))
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
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.netty_all)
    implementation(Libs.reactor_netty)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
