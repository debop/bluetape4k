configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-junit5"))

    compileOnly(Libs.kotlinx_coroutines_jdk9)
    testImplementation(Libs.kotlinx_coroutines_debug)
    testImplementation(Libs.kotlinx_coroutines_test)

    compileOnly(Libs.eclipse_collections)
    compileOnly(Libs.eclipse_collections_forkjoin)
}
