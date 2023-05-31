configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-junit5"))

    // PNG 압축 (https://github.com/depsypher/pngtastic)
    api("com.github.depsypher:pngtastic:1.7")

    testImplementation(Libs.kotlinx_coroutines_test)
}
