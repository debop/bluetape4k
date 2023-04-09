configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-utils-logging"))

    api(Libs.kotlin_test)
    api(Libs.kotlin_test_junit5)

    api(Libs.junit_jupiter)
    api(Libs.junit_jupiter_engine)
    implementation(Libs.junit_jupiter_migrationsupport)

    api(Libs.junit_platform_launcher)
    api(Libs.junit_platform_runner)

    api(Libs.mockk)
    api(Libs.kluent)

    api(Libs.slf4j_api)
    api(Libs.logback)

    compileOnly(Libs.mockserver_netty)
    compileOnly(Libs.mockserver_client_java)
    testImplementation(Libs.async_http_client)

    // For property based testing
    api(Libs.datafaker)
    api(Libs.random_beans)

    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_debug)
    testImplementation(Libs.kotlinx_coroutines_test)
}
