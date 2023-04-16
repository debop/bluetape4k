configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-kotlinx-coroutines"))
    testImplementation(project(":bluetape4k-test-junit5"))
    testImplementation(project(":bluetape4k-test-testcontainers"))

    compileOnly(Libs.redisson)
    compileOnly(Libs.lettuce_core)

    compileOnly(Libs.javax_cache_api)
    compileOnly(Libs.caffeine)
    compileOnly(Libs.caffeine_jcache)


    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(Libs.springBootStarter("data-redis"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
