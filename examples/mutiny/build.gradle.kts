configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-kotlinx-mutiny"))
    testImplementation(project(":bluetape4k-test-junit5"))

    implementation(Libs.kotlinx_atomicfu)

    // Smallrye Mutiny
    implementation(Libs.mutiny)
    implementation(Libs.mutiny_kotlin)

    // Coroutines
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)
}
