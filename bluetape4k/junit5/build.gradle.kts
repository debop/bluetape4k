configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-logging"))

    api(Libs.kotlin_test_junit5)

    api(Libs.junit_jupiter)
    api(Libs.junit_jupiter_engine)
    compileOnly(Libs.junit_jupiter_migrationsupport)
    compileOnly(Libs.junit_platform_launcher)
    compileOnly(Libs.junit_platform_runner)

    api(Libs.kluent)
    compileOnly(Libs.mockk)

    api(Libs.awaitility_kotlin)

    api(Libs.datafaker)
    api(Libs.random_beans)

    api(Libs.commons_lang3)
    compileOnly(Libs.logback)

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_test)
}