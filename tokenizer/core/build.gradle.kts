configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    testImplementation(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // Jackson
    testImplementation(project(":bluetape4k-json"))
    testImplementation(Libs.jackson_module_kotlin)
}
