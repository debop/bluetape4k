configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-aws-core"))
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-io"))
    testImplementation(project(":bluetape4k-junit5"))

    // AWS SDK V2
    api(Libs.aws2_aws_core)
    api(Libs.aws2_s3)
    api(Libs.aws2_s3_transfer_manager)
    testImplementation(Libs.aws2_test_utils)

    // S3TransferManager 가 Apple Silicon에서 생성이 안되는 제한이 있다.
    // 이를 aws-crt 를 참조하면 가능해진다 (https://github.com/aws/aws-sdk-java-v2/issues/2942)
    // https://github.com/awslabs/aws-crt-java
    // api("software.amazon.awssdk.crt:aws-crt:0.21.12")

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers_localstack)
}
