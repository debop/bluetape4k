plugins {
    kotlin("plugin.spring")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-aws-core"))
    api(project(":bluetape4k-json"))
    api(project(":bluetape4k-resilience4j"))
    api(project(":bluetape4k-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))

    // AWS SDK V2
    api(Libs.aws2_dynamodb_enhanced)
    api(Libs.aws2_netty_nio_client)
    api(Libs.aws2_aws_crt)  // AWS CRT 기반 HTTP 클라이언트를 사용하기 위해 필요합니다.
    testImplementation(Libs.aws2_test_utils)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Localstack
    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers_localstack)

    // Spring Boot
    testImplementation(Libs.springBootStarter("aop"))
    testImplementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
        exclude(module = "mockito-core")
    }

    testImplementation(Libs.javax_el)
    testImplementation(Libs.hibernate_validator)
}
