configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // Hashing
    api(Libs.zero_allocation_hashing)

    // Redis Drivers
    api(project(":bluetape4k-redis"))
    compileOnly(Libs.lettuce_core)
    compileOnly(Libs.redisson)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

    // TestContainers
    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers)
}
