configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-test-junit5"))

    // https://github.com/cowtowncoder/java-uuid-generator
    compileOnly(Libs.java_uuid_generator)

    compileOnly(Libs.commons_codec)

    testImplementation(Libs.eclipse_collections)
}
