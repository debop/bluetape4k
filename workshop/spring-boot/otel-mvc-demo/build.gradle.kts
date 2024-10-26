plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.otel.OtelMvcApplicationKt")
}


// OpenTelemetry Java Agent 를 사용할 경우 아래의 Task 를 실행하여 자동으로 다운로드 하도록 합니다.
tasks {
    test {
        jvmArgs = listOf("-javaagent:${project.layout.buildDirectory.asFile.get()}/opentelemetry-javaagent.jar")
    }
    //    bootRun {
    //        jvmArgs = listOf("-javaagent:${project.layout.buildDirectory.asFile.get()}/opentelemetry-javaagent.jar")
    //    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {

    // OpenTelemetry
    //    implementation(platform(Libs.opentelemetry_bom))
    //    implementation(platform(Libs.opentelemetry_alpha_bom))
    //    implementation(platform(Libs.opentelemetry_instrumentation_bom_alpha))

    // implementation(project(":bluetape4k-otel"))
    implementation(Libs.opentelemetry_api)
    implementation(Libs.opentelemetry_spring_boot_starter)
    implementation(Libs.opentelemetry_extension_annotations)
    implementation(Libs.opentelemetry_extension_kotlin)
    implementation(Libs.opentelemetry_exporter_otlp)
    implementation(Libs.opentelemetry_exporter_zipkin)
    implementation(Libs.opentelemetry_exporter_logging)

    implementation("io.opentelemetry.semconv:opentelemetry-semconv")

    // Bluetape4k
    implementation(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-json"))
    implementation(project(":bluetape4k-testcontainers"))
    testImplementation(project(":bluetape4k-junit5"))

    // Spring Boot
    implementation(Libs.springBootStarter("web"))
    implementation(Libs.springBootStarter("aop"))
    implementation(Libs.springBootStarter("actuator"))

    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

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

    implementation(Libs.datafaker)
}
