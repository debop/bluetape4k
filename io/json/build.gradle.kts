dependencyManagement {
    imports {
        mavenBom(Libs.jackson_bom)
        mavenBom(Libs.testcontainers_bom)
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-io-cryptography"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.javax_json_api)

    api(Libs.jackson_core)
    api(Libs.jackson_databind)
    api(Libs.jackson_datatype_jdk8)
    api(Libs.jackson_datatype_jsr310)
    // JDK 17 에서는 사용할 수 없네요
    // api(Libs.jackson_datatype_jsr353)

    api(Libs.jackson_module_kotlin)
    compileOnly(Libs.jackson_module_parameter_names)
    compileOnly(Libs.jackson_module_afterburner)

    compileOnly(Libs.jackson_dataformat_properties)
    compileOnly(Libs.jackson_dataformat_yaml)

    // Gson
    compileOnly(Libs.gson)
    compileOnly(Libs.gson_javatime_serializers)

    testImplementation(Libs.jsonpath)
    testImplementation(Libs.jsonassert)

}
