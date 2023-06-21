plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-io-json"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.spring("context-support"))
    compileOnly(Libs.spring("messaging"))
    compileOnly(Libs.spring("web"))

    compileOnly(Libs.springData("commons"))

    compileOnly(Libs.springBoot("autoconfigure"))

    api(Libs.javax_annotation_api)
    compileOnly(Libs.findbugs)

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)
}
