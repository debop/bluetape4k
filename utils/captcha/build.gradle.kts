configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-images"))
    testImplementation(project(":bluetape4k-junit5"))

    // Images
    // https://mvnrepository.com/artifact/com.sksamuel.scrimage/scrimage-core
    implementation(Libs.scrimage_core)
    implementation(Libs.scrimage_filters)
    implementation(Libs.scrimage_webp)

    testImplementation(Libs.kotlinx_coroutines_test)
}
