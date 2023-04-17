configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-kotlinx-coroutines"))
    testImplementation(project(":bluetape4k-test-junit5"))
    testImplementation(project(":bluetape4k-test-testcontainers"))

    api(Libs.javax_cache_api)

    api(Libs.caffeine)
    api(Libs.caffeine_jcache)

    compileOnly(Libs.ehcache)

    compileOnly(Libs.redisson)

    // testImplementation(Libs.springBoot("starter"))
    testImplementation(Libs.springBootStarter("cache"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude("org.junit.vintage", "junit-vintage-engine")
        exclude("junit", "junit")
    }

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)
}
