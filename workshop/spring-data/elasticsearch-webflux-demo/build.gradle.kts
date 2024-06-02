plugins {
    id(Plugins.spring_boot)
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.allopen")
    kotlin("kapt")
}

allOpen {
    annotation("org.springframework.data.elasticsearch.annotations.Document")
}
noArg {
    annotation("org.springframework.data.elasticsearch.annotations.Document")
    invokeInitializers = true
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.es.EsDemoApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.springBootStarter("aop"))
    implementation(Libs.springBootStarter("webflux"))
    implementation(Libs.springBootStarter("validation"))

    implementation(Libs.springBootStarter("data-elasticsearch"))
    implementation(Libs.elasticsearch_rest_client)

    runtimeOnly(Libs.springBoot("devtools"))
    kapt(Libs.springBoot("configuration-processor"))

    implementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
    }

    // Elasticsearch Server 관련 의존성
    implementation(Libs.testcontainers_elasticsearch)
    implementation(project(":bluetape4k-testcontainers"))

    // Swagger
    implementation(Libs.springdoc_openapi_starter_webflux_ui)
    implementation(Libs.jackson_module_kotlin)

    implementation(project(":bluetape4k-json"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    implementation(Libs.reactor_core)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
