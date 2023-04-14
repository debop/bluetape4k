configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-io-core"))
    testImplementation(project(":bluetape4k-test-junit5"))

    api(Libs.testcontainers)
    api(Libs.testcontainers_junit_jupiter)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Redis
    compileOnly(Libs.redisson)
    compileOnly(Libs.lettuce_core)

    compileOnly(Libs.kryo)
    compileOnly(Libs.lz4_java)
}
