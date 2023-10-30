plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.es.EsApplicationKt")
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

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Elasticsearch Testcontainers
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers_elasticsearch)

    // Swagger
    implementation(Libs.springdoc_openapi_starter_webflux_ui)
    implementation(Libs.jackson_module_kotlin)

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
    testImplementation(Libs.turbine)

    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-utils-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))
}
