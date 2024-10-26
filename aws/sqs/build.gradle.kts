configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-aws-core"))
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // AWS SDK V2
    api(Libs.aws2_aws_core)
    api(Libs.aws2_sqs)
    api(Libs.aws2_netty_nio_client)
    api(Libs.aws2_aws_crt)  // AWS CRT 기반 HTTP 클라이언트를 사용하기 위해 필요합니다.
    testImplementation(Libs.aws2_test_utils)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers_localstack)
}
