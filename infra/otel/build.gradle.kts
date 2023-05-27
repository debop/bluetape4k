import de.undercouch.gradle.tasks.download.Download

plugins {
    kotlin("plugin.spring")
    id(Plugins.shadow)

    id("de.undercouch.download") version "5.4.0"
}

// OpenTelemetry Java Agent 를 사용할 경우 아래의 Task 를 실행하여 자동으로 다운로드 하도록 합니다.
tasks {
    test {
        dependsOn("downloadAgent")
        jvmArgs = listOf("-javaagent:${project.buildDir}/${Libs.opentelemetry_javaagent_local_path}")
    }

    // Download the OpenTelemetry java agent and put it in the build directory
    task<Download>("downloadAgent") {
        src(Libs.opentelemetry_javaagent_remote_path)
        dest("${project.buildDir}/${Libs.opentelemetry_javaagent_local_path}")
        onlyIfModified(true)
        onlyIfNewer(true)
        download()
    }
}

configurations {
    // compileOnly 나 runtimeOnly로 지정된 Dependency를 testImplementation 으로도 지정하도록 합니다.
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // OpenTelemetry
    api(Libs.opentelemetry_api)
    api(Libs.opentelemetry_sdk)
    api(Libs.opentelemetry_extensions_kotlin)
    compileOnly(Libs.opentelemetry_sdk_extensions_autoconfigure)
    compileOnly(Libs.opentelemetry_sdk_metrics)
    compileOnly(Libs.opentelemetry_sdk_trace)
    compileOnly(Libs.opentelemetry_sdk_testing)

    compileOnly(Libs.opentelemetry_exporter_logging)
    // logback mdc 로 otel 정보를 전달하는 라이브러리
    // https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/logback/logback-mdc-1.0/library
    testRuntimeOnly(Libs.opentelemetry_logback_mdc_1_0)
    // otel 이 jul 을 사용해서 로그를 남기는데, 이를 slf4j 로 전달해주는 라이브러리
    compileOnly(Libs.jul_to_slf4j)


    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_slf4j)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
