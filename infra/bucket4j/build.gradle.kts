configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-junit5"))

    // Bucket4j
    api(Libs.bucket4j_core)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
