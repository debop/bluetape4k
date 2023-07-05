configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

    api(Libs.eclipse_collections)
    compileOnly(Libs.eclipse_collections_forkjoin)
}
