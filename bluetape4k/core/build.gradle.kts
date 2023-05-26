configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-logging"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.kotlinx_atomicfu)

    // Apache Commons
    api(Libs.commons_lang3)
    compileOnly(Libs.commons_codec)
    // compileOnly(Libs.commons_text)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Eclipse Collections
    api(Libs.eclipse_collections)
    compileOnly(Libs.eclipse_collections_forkjoin)
    testImplementation(Libs.eclipse_collections_testutils)

    // Cache
    compileOnly(Libs.caffeine)
    compileOnly(Libs.caffeine_jcache)
}
