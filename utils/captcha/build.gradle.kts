configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-images"))
    testImplementation(project(":bluetape4k-junit5"))

    // Images
    // https://mvnrepository.com/artifact/com.sksamuel.scrimage/scrimage-core
    api(Libs.scrimage_core)
    api(Libs.scrimage_filters)
    api(Libs.scrimage_webp)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
