dependencies {

    implementation(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    testImplementation(Libs.redisson)

    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_junit_jupiter)
}
