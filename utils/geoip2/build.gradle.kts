configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-io-json"))
    testImplementation(project(":bluetape4k-junit5"))

    // MaxMind GeoIP2
    implementation("com.maxmind.geoip2:geoip2:4.0.1")
}
