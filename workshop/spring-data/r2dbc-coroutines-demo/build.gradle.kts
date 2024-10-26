plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.r2dbc.ApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-spring-support"))
    testImplementation(project(":bluetape4k-junit5"))

    // PostgreSql Server
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers_postgresql)

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.reactor_core)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)

    // R2DBC
    implementation(project(":bluetape4k-r2dbc"))
    implementation(Libs.springBootStarter("data-r2dbc"))
    implementation(Libs.r2dbc_postgresql)
    implementation(Libs.r2dbc_h2)
    implementation(Libs.r2dbc_pool)

    implementation(Libs.springBootStarter("webflux"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

}
