configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-utils-idgenerators"))
    compileOnly(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-io-netty"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    api(Libs.javax_cache_api)

    // Cache Providers
    compileOnly(Libs.cache2k_core)
    compileOnly(Libs.cache2k_jcache)

    api(Libs.caffeine)
    api(Libs.caffeine_jcache)

    compileOnly(Libs.ehcache)
    compileOnly(Libs.ehcache_clustered)
    compileOnly(Libs.ehcache_transactions)

    compileOnly(Libs.redisson)

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(Libs.springBootStarter("cache"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude("org.junit.vintage", "junit-vintage-engine")
        exclude("junit", "junit")
    }
}
