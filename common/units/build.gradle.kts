configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":common-core"))
    api(Libs.commons_lang3)
    compileOnly(Libs.commons_codec)
    compileOnly(Libs.commons_text)
}
