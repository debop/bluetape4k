configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(Libs.okio)
    testImplementation(Libs.okio_fakefilesystem)

    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-cryptography"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    compileOnly(project(":bluetape4k-coroutines"))
    compileOnly(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
