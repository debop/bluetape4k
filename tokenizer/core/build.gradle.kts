configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    testImplementation(project(":bluetape4k-test-junit5"))

    testImplementation(Libs.jackson_module_kotlin)
}
