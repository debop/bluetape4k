configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-io-core"))
    testImplementation(project(":bluetape4k-test-junit5"))

    api(Libs.jakarta_json_api)

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

    // cryptography
    compileOnly(Libs.jasypt)
    compileOnly(Libs.bouncycastle_bcprov)
    compileOnly(Libs.bouncycastle_bcpkix)
}
