configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-utils-idgenerators"))

    compileOnly(Libs.kotlinx_atomicfu)

    // Smallrye Mutiny
    api(Libs.mutiny)
    api(Libs.mutiny_kotlin)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

}
