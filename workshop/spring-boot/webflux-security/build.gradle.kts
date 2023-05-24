plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.security.server.ApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-utils-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("configuration-processor"))
    kapt(Libs.springBoot("autoconfigure-processor"))

    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("webflux"))
    implementation(Libs.springBootStarter("actuator"))

    implementation(Libs.springBootStarter("security"))
    testImplementation(Libs.springSecurity("test"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // MongoDB
    implementation(Libs.springBootStarter("data-mongodb-reactive"))
    implementation(Libs.mongodb_driver_sync)
    implementation(Libs.mongodb_driver_reactivestreams)
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers_mongodb)

    // JWT
    // TODO: Spring Security에서 제공하는 JWT 라이브러리를 사용하도록 변경
    implementation(project(":bluetape4k-utils-jwt"))
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    // Validation
    implementation(Libs.javax_validation_api)
    implementation(Libs.hibernate_validator)
    implementation(Libs.springBootStarter("validation"))

    // Coroutines
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_jdk8)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(project(":bluetape4k-io-netty"))
    implementation(Libs.reactor_netty)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
