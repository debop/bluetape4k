configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-tokenizer-korean"))
    testImplementation(project(":bluetape4k-test-junit5"))
}
