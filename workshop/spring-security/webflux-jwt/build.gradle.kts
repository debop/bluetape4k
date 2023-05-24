plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.security.jwt.JwtApplicationKt")
}


// NOTE: implementation 나 runtimeOnly로 지정된 Dependency를 testimplementation 으로도 지정하도록 합니다.
configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-spring-support"))
    testImplementation(project(":bluetape4k-io-json"))
    testImplementation(project(":bluetape4k-junit5"))

    // Spring Security
    implementation(Libs.springBootStarter("security"))
    implementation(Libs.springBootStarter("oauth2-resource-server"))
    testImplementation(Libs.springSecurity("test"))

    implementation(Libs.springBootStarter("webflux"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation(Libs.okhttp3_mockwebserver)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    compileOnly(Libs.reactor_core)
    compileOnly(Libs.reactor_kotlin_extensions)
    compileOnly(Libs.reactor_test)
}
