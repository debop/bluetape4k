configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // https://github.com/cowtowncoder/java-uuid-generator
    api(Libs.java_uuid_generator)

    api(Libs.commons_codec)

    testImplementation(Libs.eclipse_collections)
}
