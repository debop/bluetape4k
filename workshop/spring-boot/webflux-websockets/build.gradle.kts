plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.webflux.SampleApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-io-netty"))
    implementation(project(":bluetape4k-utils-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("webflux"))
    implementation(Libs.springCloudStarter("sleuth"))

    implementation(Libs.webjar("webjars-locator-core", "0.52"))
    implementation(Libs.webjar("bootstrap", "5.2.3"))
    implementation(Libs.webjar("jquery", "3.6.4"))
    implementation(Libs.webjar("font-awesome", "6.4.0"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Coroutines
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    testImplementation(Libs.reactor_test)
}
