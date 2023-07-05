plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.webflux.SampleApplicationKt")
}

dependencyManagement {
    imports {
        mavenBom(Libs.spring_cloud_dependencies)
        mavenBom(Libs.spring_boot_dependencies)
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-spring-support"))
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
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
