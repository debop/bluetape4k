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
    kotlin("plugin.atomicfu") version Versions.kotlin
    kotlin("kapt") version Versions.kotlin apply false

    id(Plugins.dependency_management) version Plugins.Versions.dependency_management
    id(Plugins.spring_boot) version Plugins.Versions.spring_boot apply false

    id(Plugins.quarkus) version Plugins.Versions.quarkus apply false

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
        mavenLocal()
        mavenCentral()
        google()
    }
}

subprojects {
    if (name == "bluetape4k-bom") {
        return@subprojects
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
            options.isIncremental = true
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                incremental = true
                javaParameters = true
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
                    // "-Xuse-k2"                   // since Kotlin 1.7  // kapt not support
                )

                val experimentalAnnotations = listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.contracts.ExperimentalContracts",
                    "kotlin.experimental.ExperimentalTypeInference",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.InternalCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview",
                    "kotlinx.coroutines.DelicateCoroutinesApi",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                incremental = true
                javaParameters = true
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
                    "kotlinx.coroutines.DelicateCoroutinesApi",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }

        test {
            useJUnitPlatform()

            // 테스트 시 아래와 같은 예외 메시지를 제거하기 위해서 
            // OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
            jvmArgs("-Xshare:off")

            if (project.name.contains("quarkus")) {
                // [Quarkus Logging](https://quarkus.io/guides/logging)
                systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
            }

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
            mavenBom(Libs.testcontainers_bom)
            mavenBom(Libs.junit_bom)
            mavenBom(Libs.aws_bom)
            mavenBom(Libs.aws2_bom)
            mavenBom(Libs.okhttp3_bom)
            mavenBom(Libs.grpc_bom)
            mavenBom(Libs.protobuf_bom)
            mavenBom(Libs.metrics_bom)
            mavenBom(Libs.fabric8_kubernetes_client_bom)
            mavenBom(Libs.resilience4j_bom)
            mavenBom(Libs.netty_bom)
            mavenBom(Libs.jackson_bom)

            mavenBom(Libs.kotlinx_coroutines_bom)
            mavenBom(Libs.kotlin_bom)
        }
        dependencies {
            dependency(Libs.jetbrains_annotations)

            // Kotlinx Coroutines (mavenBom 이 적용이 안되어서 추가로 명시했습니다)
            dependency(Libs.kotlinx_coroutines_bom)
            dependency(Libs.kotlinx_coroutines_core)
            dependency(Libs.kotlinx_coroutines_core_jvm)
            dependency(Libs.kotlinx_coroutines_jdk8)
            dependency(Libs.kotlinx_coroutines_jdk9)
            dependency(Libs.kotlinx_coroutines_jdk8)
            dependency(Libs.kotlinx_coroutines_reactive)
            dependency(Libs.kotlinx_coroutines_reactor)
            dependency(Libs.kotlinx_coroutines_rx2)
            dependency(Libs.kotlinx_coroutines_rx3)
            dependency(Libs.kotlinx_coroutines_slf4j)
            dependency(Libs.kotlinx_coroutines_debug)
            dependency(Libs.kotlinx_coroutines_test)
            dependency(Libs.kotlinx_coroutines_test_jvm)

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

            // Javax API
            dependency(Libs.javax_activation_api)
            dependency(Libs.javax_annotation_api)
            dependency(Libs.javax_el_api)
            dependency(Libs.javax_cache_api)
            dependency(Libs.javax_inject)
            dependency(Libs.javax_json_api)
            dependency(Libs.javax_persistence_api)
            dependency(Libs.javax_servlet_api)
            dependency(Libs.javax_transaction_api)
            dependency(Libs.javax_validation_api)
            dependency(Libs.javax_ws_rs_api)

            // 현재 Quarkus 에서만 jakarta api 를 사용하는데, Quarkus 가 사용하는 버전을 사용하도록 한다.
            // Jakarta 는 명시적으로는 사용하지 않도록 한다 

            // Compressor
            dependency(Libs.snappy_java)
            dependency(Libs.lz4_java)
            dependency(Libs.zstd_jni)

            // Java Money
            dependency(Libs.javax_money_api)
            dependency(Libs.javamoney_moneta)

            dependency(Libs.findbugs)
            dependency(Libs.guava)

            dependency(Libs.kryo)
            dependency(Libs.marshalling)
            dependency(Libs.marshalling_river)

            // Jackson (이상하게 mavenBom 에 적용이 안되어서 강제로 추가하였다)
            dependency(Libs.jackson_bom)
            dependency(Libs.jackson_annotations)
            dependency(Libs.jackson_core)
            dependency(Libs.jackson_databind)
            dependency(Libs.jackson_datatype_jdk8)
            dependency(Libs.jackson_datatype_jsr310)
            dependency(Libs.jackson_datatype_jsr353)
            dependency(Libs.jackson_module_kotlin)
            dependency(Libs.jackson_module_paranamer)
            dependency(Libs.jackson_module_parameter_names)
            dependency(Libs.jackson_module_afterburner)

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

            // Http
            dependency(Libs.async_http_client)
            dependency(Libs.async_http_client_extras_retrofit2)
            dependency(Libs.async_http_client_extras_rxjava2)

            dependency(Libs.httpclient5)
            dependency(Libs.httpcore5)
            dependency(Libs.httpcore5_h2)
            dependency(Libs.httpcore5_reactive)

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
            dependency(Libs.mysql_connector_j)
            dependency(Libs.mariadb_java_client)

            dependency(Libs.caffeine)
            dependency(Libs.caffeine_jcache)

            dependency(Libs.objenesis)
            dependency(Libs.ow2_asm)

            dependency(Libs.reflectasm)

            dependency(Libs.junit_bom)
            dependency(Libs.junit_jupiter)
            dependency(Libs.junit_jupiter_api)
            dependency(Libs.junit_jupiter_engine)
            dependency(Libs.junit_jupiter_migrationsupport)
            dependency(Libs.junit_jupiter_params)
            dependency(Libs.junit_platform_commons)
            dependency(Libs.junit_platform_engine)
            dependency(Libs.junit_platform_launcher)
            dependency(Libs.junit_platform_runner)

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

            // Prometheus
            dependency(Libs.prometheus_simpleclient)
            dependency(Libs.prometheus_simpleclient_common)
            dependency(Libs.prometheus_simpleclient_httpserver)
            dependency(Libs.prometheus_simpleclient_pushgateway)
            dependency(Libs.prometheus_simpleclient_spring_boot)
            dependency(Libs.prometheus_simpleclient_tracer_common)
            dependency(Libs.prometheus_simpleclient_tracer_otel)
            dependency(Libs.prometheus_simpleclient_tracer_otel_agent)

            // OW2 ASM
            dependency(Libs.ow2_asm)
            dependency(Libs.ow2_asm_commons)
            dependency(Libs.ow2_asm_util)
            dependency(Libs.ow2_asm_tree)

            dependency(Libs.snakeyaml)
            dependency(Libs.jna)
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
        api(Libs.kotlin_reflect)
        api(Libs.kotlinx_atomicfu)
        testImplementation(Libs.kotlin_test)
        testImplementation(Libs.kotlin_test_junit5)

        compileOnly(Libs.kotlinx_coroutines_core)

        // 개발 시에는 logback 이 검증하기에 더 좋고, Production에서 비동기 로깅은 log4j2 가 성능이 좋다고 합니다.
        api(Libs.slf4j_api)
        testImplementation(Libs.logback)
        testImplementation(Libs.jcl_over_slf4j)
        testImplementation(Libs.jul_to_slf4j)
        testImplementation(Libs.log4j_over_slf4j)

        testImplementation(Libs.junit_jupiter)
        testImplementation(Libs.junit_jupiter_migrationsupport)
        testRuntimeOnly(Libs.junit_platform_engine)
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
