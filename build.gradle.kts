import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

plugins {
    base
    `maven-publish`
    jacoco
    kotlin("jvm") version Versions.kotlin

    // see: https://kotlinlang.org/docs/reference/compiler-plugins.html
    kotlin("plugin.spring") version Versions.kotlin apply false
    kotlin("plugin.allopen") version Versions.kotlin apply false
    kotlin("plugin.noarg") version Versions.kotlin apply false
    kotlin("plugin.jpa") version Versions.kotlin apply false
    kotlin("plugin.serialization") version Versions.kotlin apply false
    // kotlin("plugin.atomicfu") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false

    id(Plugins.dependency_management) version Plugins.Versions.dependency_management
    id(Plugins.spring_boot) version Plugins.Versions.spring_boot apply false

    id(Plugins.dokka) version Plugins.Versions.dokka
    id(Plugins.testLogger) version Plugins.Versions.testLogger
    id(Plugins.shadow) version Plugins.Versions.shadow apply false
}

val projectGroup: String by project
val baseVersion: String by project
val snapshotVersion: String by project

allprojects {

    group = projectGroup
    version = baseVersion + snapshotVersion

    repositories {
        mavenCentral()
        google()

        // for Kotlinx Benchmark
        maven {
            name = "Kotlinx"
            url = uri("https://dl.bintray.com/kotlin/kotlinx/")
        }

        // for Oracle ojdbc10
//        maven {
//            name = "ICM"
//            url = uri("https://maven.icm.edu.pl/artifactory/repo/")
//        }

        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
    }
}

subprojects {
    if (name == "bluetape4k-bom") {
        return@subprojects
    }

    configurations.all {
        if (name.lowercase().contains("kapt") || name.lowercase().contains("proto")) {
            attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        }
    }
    configurations.forEach {
        if (it.name.contains("productionRuntimeClasspath")) {
            it.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        }
    }

    apply {
        plugin<JavaLibraryPlugin>()
        plugin<KotlinPlatformJvmPlugin>()

        plugin("jacoco")
        plugin("maven-publish")

        plugin(Plugins.dependency_management)

        plugin(Plugins.dokka)
        plugin(Plugins.testLogger)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    val javaVersion = JavaVersion.VERSION_17.toString()

    tasks {

        compileJava {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                languageVersion = "1.8"
                apiVersion = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xinline-classes",
                    "-Xallow-result-return-type",
                    "-Xstring-concat=indy",         // since Kotlin 1.4.20 for JVM 9+
                    "-progressive",                 // since Kotlin 1.6
                    "-Xenable-builder-inference",   // since Kotlin 1.6
                    "-Xbackend-threads=0",          // since 1.6.20 (0 means one thread per CPU core)
                    // "-Xuse-k2"                      // since Kotlin 1.7  // kapt not support
                )

                val experimentalAnnotations = listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.contracts.ExperimentalContracts",
                    "kotlin.experimental.ExperimentalTypeInference",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.InternalCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                languageVersion = "1.8"
                apiVersion = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xinline-classes",
                    "-Xallow-result-return-type",
                    "-Xstring-concat=indy",         // since Kotlin 1.4.20 for JVM 9+
                    "-progressive",                 // since Kotlin 1.6
                    "-Xenable-builder-inference",   // since Kotlin 1.6
                    "-Xbackend-threads=0",          // since 1.6.20 (0 means one thread per CPU core)
                    // "-Xuse-k2"                      // since Kotlin 1.7 // kapt not support
                )

                val experimentalAnnotations = listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.Experimental",
                    "kotlin.ExperimentalStdlibApi",
                    "kotlin.time.ExperimentalTime",
                    "kotlin.contracts.ExperimentalContracts",
                    // "kotlin.experimental.ExperimentalTypeInference",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.InternalCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }

        test {
            useJUnitPlatform()

            testLogging {
                showExceptions = true
                showCauses = true
                showStackTraces = true

                events("failed")
            }
        }

        testlogger {
            theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
            showFullStackTraces = true
        }

        jacoco {
            toolVersion = Plugins.Versions.jacoco
        }

        jacocoTestReport {
            reports {
                html.required.set(true)
                xml.required.set(true)
            }
        }

        jacocoTestCoverageVerification {
            dependsOn(jacocoTestReport)

            violationRules {
                rule {
                    // 룰 검증 수행 여부
                    enabled = true

                    // 룰을 검증할 단위를 클래스 단위로 한다
                    element = "CLASS"         // BUNDLE|PACKAGE|CLASS|SOURCEFILE|METHOD

                    // 브랜치 커버리지를 최소한 10% 를 만족시켜야 한다
                    limit {
                        counter =
                            "INSTRUCTION"       // INSTRUCTION, LINE, BRANCH, COMPLEXITY, METHOD and CLASS. Defaults to INSTRUCTION.
                        value =
                            "COVEREDRATIO"   // TOTALCOUNT, MISSEDCOUNT, COVEREDCOUNT, MISSEDRATIO and COVEREDRATIO. Defaults to COVEREDRATIO
                        minimum = 0.10.toBigDecimal()
                    }
                }
            }
        }

        jacocoTestCoverageVerification {
            dependsOn(jacocoTestReport)

            violationRules {
                rule {
                    // 룰 검증 수행 여부
                    enabled = true

                    // 룰을 검증할 단위를 클래스 단위로 한다
                    element = "CLASS"         // BUNDLE|PACKAGE|CLASS|SOURCEFILE|METHOD

                    // 브랜치 커버리지를 최소한 10% 를 만족시켜야 한다
                    limit {
                        counter =
                            "INSTRUCTION"       // INSTRUCTION, LINE, BRANCH, COMPLEXITY, METHOD and CLASS. Defaults to INSTRUCTION.
                        value =
                            "COVEREDRATIO"   // TOTALCOUNT, MISSEDCOUNT, COVEREDCOUNT, MISSEDRATIO and COVEREDRATIO. Defaults to COVEREDRATIO
                        minimum = 0.10.toBigDecimal()
                    }
                }
            }
        }

        jar {
            manifest.attributes["Specification-Title"] = project.name
            manifest.attributes["Specification-Version"] = project.version
            manifest.attributes["Implementation-Title"] = project.name
            manifest.attributes["Implementation-Version"] = project.version
            manifest.attributes["Automatic-Module-Name"] = project.name.replace('-', '.')
            manifest.attributes["Created-By"] =
                "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
        }

        // https://kotlin.github.io/dokka/1.6.0/user_guide/gradle/usage/
        withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
            outputDirectory.set(buildDir.resolve("javadoc"))
            dokkaSourceSets {
                configureEach {
                    includes.from("README.md")
                }
            }
        }

        dokkaHtml.configure {
            outputDirectory.set(buildDir.resolve("dokka"))
        }

        clean {
            doLast {
                delete("./.project")
                delete("./out")
                delete("./bin")
            }
        }
    }

    dependencyManagement {
        imports {
            mavenBom(Libs.feign_bom)
            mavenBom(Libs.micrometer_bom)
            mavenBom(Libs.micrometer_tracing_bom)
            mavenBom(Libs.opentelemetry_bom)
            mavenBom(Libs.opentelemetry_alpha_bom)
            mavenBom(Libs.opentelemetry_instrumentation_bom_alpha)
            mavenBom(Libs.spring_cloud_dependencies)
            mavenBom(Libs.spring_boot_dependencies)
            mavenBom(Libs.vertx_dependencies)
            mavenBom(Libs.log4j_bom)
            mavenBom(Libs.junit_bom)
            mavenBom(Libs.testcontainers_bom)
            mavenBom(Libs.aws_bom)
            mavenBom(Libs.aws2_bom)
            mavenBom(Libs.okhttp3_bom)
            mavenBom(Libs.grpc_bom)
            mavenBom(Libs.protobuf_bom)
            mavenBom(Libs.metrics_bom)
            mavenBom(Libs.fabric8_kubernetes_client_bom)
            mavenBom(Libs.netty_bom)
            mavenBom(Libs.jackson_bom)

            mavenBom(Libs.kotlinx_coroutines_bom)
            mavenBom(Libs.kotlin_bom)
        }
        dependencies {
            dependency(Libs.jetbrains_annotations)

            // Apache Commons
            dependency(Libs.commons_beanutils)
            dependency(Libs.commons_collections4)
            dependency(Libs.commons_compress)
            dependency(Libs.commons_codec)
            dependency(Libs.commons_csv)
            dependency(Libs.commons_lang3)
            dependency(Libs.commons_logging)
            dependency(Libs.commons_math3)
            dependency(Libs.commons_pool2)
            dependency(Libs.commons_text)
            dependency(Libs.commons_exec)
            dependency(Libs.commons_io)

            dependency(Libs.slf4j_api)
            dependency(Libs.jcl_over_slf4j)
            dependency(Libs.jul_to_slf4j)
            dependency(Libs.log4j_over_slf4j)
            dependency(Libs.logback)
            dependency(Libs.logback_core)

            // Jakarta API
            dependency(Libs.jakarta_activation_api)
            dependency(Libs.jakarta_annotation_api)
            dependency(Libs.jakarta_el_api)
            dependency(Libs.jakarta_inject_api)
            dependency(Libs.jakarta_interceptor_api)
            dependency(Libs.jakarta_jms_api)
            dependency(Libs.jakarta_json_api)
            dependency(Libs.jakarta_persistence_api)
            dependency(Libs.jakarta_servlet_api)
            dependency(Libs.jakarta_transaction_api)
            dependency(Libs.jakarta_validation_api)

            // Compressor
            dependency(Libs.snappy_java)
            dependency(Libs.lz4_java)
            dependency(Libs.zstd_jni)

            // Java Money
            dependency(Libs.javax_money_api)
            dependency(Libs.javamoney_moneta)

            dependency(Libs.findbugs)
            dependency(Libs.guava)
            dependency(Libs.joda_time)

            dependency(Libs.fst)
            dependency(Libs.kryo)
            dependency(Libs.marshalling)
            dependency(Libs.marshalling_river)

            // Retrofit
            dependency(Libs.retrofit2)
            dependency(Libs.retrofit2_adapter_java8)
            dependency(Libs.retrofit2_adapter_reactor)
            dependency(Libs.retrofit2_adapter_rxjava2)
            dependency(Libs.retrofit2_converter_jackson)
            dependency(Libs.retrofit2_converter_moshi)
            dependency(Libs.retrofit2_converter_protobuf)
            dependency(Libs.retrofit2_converter_scalars)
            dependency(Libs.retrofit2_mock)

            // Resilience4j
            dependency(Libs.resilience4j_all)
            dependency(Libs.resilience4j_annotations)
            dependency(Libs.resilience4j_bulkhead)
            dependency(Libs.resilience4j_cache)
            dependency(Libs.resilience4j_circuitbreaker)
            dependency(Libs.resilience4j_circularbuffer)
            dependency(Libs.resilience4j_consumer)
            dependency(Libs.resilience4j_core)
            dependency(Libs.resilience4j_feign)
            dependency(Libs.resilience4j_framework_common)
            dependency(Libs.resilience4j_kotlin)
            dependency(Libs.resilience4j_metrics)
            dependency(Libs.resilience4j_micrometer)
            dependency(Libs.resilience4j_prometheus)
            dependency(Libs.resilience4j_ratelimiter)
            dependency(Libs.resilience4j_ratpack)
            dependency(Libs.resilience4j_reactor)
            dependency(Libs.resilience4j_retrofit)
            dependency(Libs.resilience4j_retry)
            dependency(Libs.resilience4j_rxjava2)
            dependency(Libs.resilience4j_rxjava3)
            dependency(Libs.resilience4j_spring)
            dependency(Libs.resilience4j_spring_boot2)
            dependency(Libs.resilience4j_spring_cloud2)
            dependency(Libs.resilience4j_timelimiter)
            dependency(Libs.resilience4j_vertx)

            // Http
            dependency(Libs.async_http_client)
            dependency(Libs.async_http_client_extras_retrofit2)
            dependency(Libs.async_http_client_extras_rxjava2)

            dependency(Libs.grpc_kotlin_stub)

            dependency(Libs.mongo_bson)
            dependency(Libs.mongodb_driver_core)
            dependency(Libs.mongodb_driver_reactivestreams)

            // Hibernate
            dependency(Libs.hibernate_core)
            dependency(Libs.hibernate_jcache)
            dependency(Libs.javassist)

            dependency(Libs.querydsl_apt)
            dependency(Libs.querydsl_core)
            dependency(Libs.querydsl_jpa)

            // Validators
            dependency(Libs.javax_validation_api)
            dependency(Libs.hibernate_validator)
            dependency(Libs.hibernate_validator_annotation_processor)
            dependency(Libs.javax_el)

            dependency(Libs.hikaricp)
            dependency(Libs.mysql_connector_java)
            dependency(Libs.mariadb_java_client)

            dependency(Libs.cache2k_api)
            dependency(Libs.cache2k_core)
            dependency(Libs.cache2k_jcache)
            dependency(Libs.cache2k_spring)

            dependency(Libs.caffeine)
            dependency(Libs.caffeine_jcache)

            // Metrics
            dependency(Libs.latencyUtils)
            dependency(Libs.hdrHistogram)

            dependency(Libs.objenesis)
            dependency(Libs.ow2_asm)

            dependency(Libs.reflectasm)

            dependency(Libs.kluent)
            dependency(Libs.assertj_core)

            dependency(Libs.mockk)
            dependency(Libs.mockito_core)
            dependency(Libs.mockito_junit_jupiter)
            dependency(Libs.mockito_kotlin)

            dependency(Libs.datafaker)
            dependency(Libs.random_beans)

            dependency(Libs.jsonpath)
            dependency(Libs.jsonassert)

            dependency(Libs.bouncycastle_bcpkix)
            dependency(Libs.bouncycastle_bcprov)

            dependency(Libs.prometheus_simpleclient)
            dependency(Libs.prometheus_simpleclient_common)
            dependency(Libs.prometheus_simpleclient_httpserver)
            dependency(Libs.prometheus_simpleclient_pushgateway)
            dependency(Libs.prometheus_simpleclient_spring_boot)

            // OW2 ASM
            dependency(Libs.ow2_asm)
            dependency(Libs.ow2_asm_commons)
            dependency(Libs.ow2_asm_util)
            dependency(Libs.ow2_asm_tree)
        }
    }

    dependencies {
        val api by configurations
        val testApi by configurations
        val implementation by configurations
        val testImplementation by configurations

        val compileOnly by configurations
        val testCompileOnly by configurations
        val testRuntimeOnly by configurations

        api(Libs.jetbrains_annotations)

        api(Libs.kotlin_stdlib)
        api(Libs.kotlin_stdlib_jdk8)
        implementation(Libs.kotlin_reflect)
        testImplementation(Libs.kotlin_test)
        testImplementation(Libs.kotlin_test_junit5)

        // 개발 시에는 logback 이 검증하기에 더 좋고, Production에서 비동기 로깅은 log4j2 가 성능이 좋다고 합니다.
        api(Libs.slf4j_api)
        testImplementation(Libs.logback)

        testImplementation(Libs.junit_jupiter)
        testImplementation(Libs.junit_jupiter_migrationsupport)
        testRuntimeOnly(Libs.junit_platform_engine)

        testImplementation(Libs.kluent)
        testImplementation(Libs.mockk)
        testImplementation(Libs.awaitility_kotlin)
        // testImplementation(Libs.testcontainers)
        // Apple Silicon에서 testcontainers 를 사용하기 위해 참조해야 합니다.
        // testImplementation(Libs.jna)

        // Property baesd test
        testImplementation(Libs.datafaker)
        testImplementation(Libs.random_beans)
    }

    tasks.withType<Jar> {
        manifest.attributes["Specification-Title"] = project.name
        manifest.attributes["Specification-Version"] = project.version
        manifest.attributes["Implementation-Title"] = project.name
        manifest.attributes["Implementation-Version"] = project.version
        manifest.attributes["Automatic-Module-Name"] = project.name.replace('-', '.')
        manifest.attributes["Created-By"] =
            "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
    }

    /*
        1. mavenLocal 에 publish 시에는 ./gradlew publishMavenPublicationToMavenLocalRepository 를 수행
        2. nexus에 publish 시에는 ./gradlew publish -PnexusDeployPassword=디플로이 로 비밀번호를 넣어줘야 배포가 됩니다.

        Release 를 위해서는 아래와 같이 `nexusDeployPassword`에 비밀번호를 넣고, `snapshotVersion`에 아무 것도 지정하지 않으면
        nexus server의 releases 에 등록됩니다.
        
        ```bash
        $ ./gradlew clean build
        $ ./gradlew publish -PnexusDeployPassword=elvmffhdl -PsnapshotVersion=
        ```
     */
    publishing {
        publications {
            if (!project.path.contains("sample")) {
                create<MavenPublication>("Maven") {
                    val binaryJar = components["java"]

                    val sourcesJar by tasks.creating(Jar::class) {
                        archiveClassifier.set("sources")
                        from(sourceSets["main"].allSource)
                    }

                    val javadocJar by tasks.creating(Jar::class) {
                        archiveClassifier.set("javadoc")
                        from("$buildDir/javadoc")
                    }

                    from(binaryJar)
                    artifact(sourcesJar)
                    artifact(javadocJar)

                    pom {
                        name.set("bluetape4k")
                        description.set("BlueTape for Kotlin")
                        url.set("https://github.com/debop")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("debop")
                                name.set("Sunghyouk Bae")
                                email.set("sunghyouk.bae@gmail.com")
                            }
                        }
                        scm {
                            url.set("https://www.github.com/debop/bluetape4k")
                            connection.set("scm:git:https://www.github.com/debop/bluetape4k")
                            developerConnection.set("scm:git:https://www.github.com/debop")
                        }
                    }
                }
            }
        }
        repositories {
            mavenLocal()
        }
    }
}
