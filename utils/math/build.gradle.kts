configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-cache"))
    testImplementation(project(":bluetape4k-times"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.commons_math3)
    api(Libs.commons_collections4)
    api(Libs.eclipse_collections)

    // Random Number Generator
    compileOnly(Libs.commons_digest3)
    compileOnly(Libs.commons_rng_simple)
}
