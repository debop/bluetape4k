plugins {
    kotlin("kapt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    kapt(Libs.javax_annotation_api)
    kaptTest(Libs.javax_annotation_api)

    api(Libs.mapstruct)
    kapt(Libs.mapstruct_processor)
    kaptTest(Libs.mapstruct_processor)
}
