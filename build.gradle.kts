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
    kotlin("kapt") version Versions.kotlin apply false

    id(BuildPlugins.dependency_management) version BuildPlugins.Versions.dependency_management
    id(BuildPlugins.spring_boot) version BuildPlugins.Versions.spring_boot apply false

    id(BuildPlugins.testLogger) version BuildPlugins.Versions.testLogger
    id(BuildPlugins.dokka) version BuildPlugins.Versions.dokka
}

val projectGroup: String by project
val baseVersion: String by project
val snapshotVersion: String by project

allprojects {

    group = projectGroup
    version = baseVersion + snapshotVersion

    repositories {
        mavenCentral()
        jcenter()
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
    if (name == "kommons-bom") {
        return@subprojects
    }

    configurations.all {
        if (name.toLowerCase().contains("kapt") || name.toLowerCase().contains("proto")) {
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

        plugin(BuildPlugins.dependency_management)

        plugin(BuildPlugins.testLogger)
        plugin(BuildPlugins.dokka)
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
                languageVersion = "1.7"
                apiVersion = "1.7"
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
                languageVersion = "1.7"
                apiVersion = "1.7"
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
                    "kotlin.experimental.ExperimentalTypeInference",
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
            showSummary = true
        }

        // Configure existing Dokka task to output HTML to typical Javadoc directory
        dokka {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"

            // externalDocumentationLink { url = URL("https://docs.oracle.com/javase/8/docs/api/") }
            // commons-io 가 제대로 Link 되는데도, dokka에서 예외를 발생시킨다. 우선은 link 안되게 막음
            // externalDocumentationLink { url = URL("https://commons.apache.org/proper/commons-io/javadocs/api-2.6/") }
        }

//        jacocoTestReport {
//            reports {
//                html.isEnabled = true
//                xml.isEnabled = true
//            }
//        }

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
            mavenBom(Libs.spring_boot_dependencies)
            mavenBom(Libs.spring_cloud_dependencies)
            mavenBom(Libs.log4j_bom)
            mavenBom(Libs.testcontainers_bom)
            mavenBom(Libs.netty_bom)
            mavenBom(Libs.jackson_bom)
            mavenBom(Libs.aws_bom)
            mavenBom(Libs.okhttp3_bom)
            mavenBom(Libs.grpc_bom)
            mavenBom(Libs.protobuf_bom)
            mavenBom(Libs.metrics_bom)
            mavenBom(Libs.micrometer_bom)
            mavenBom(Libs.resilience4j_bom)

            mavenBom(Libs.kotlin_bom)
            mavenBom(Libs.kotlinx_coroutines_bom)
        }
        dependencies {
            dependency(Libs.jetbrains_annotations)

            dependency(Libs.kotlinx_io)
            dependency(Libs.kotlinx_io_jvm)
            dependency(Libs.kotlinx_coroutines_io_jvm)

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
            dependency(Libs.logback)

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
            dependency(Libs.kryo_serializers)

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

            dependency(Libs.typesafe_config)
            dependency(Libs.grpc_kotlin_stub)

            dependency(Libs.rxjava2)
            dependency(Libs.rxkotlin2)

            dependency(Libs.mongo_java_driver)
            dependency(Libs.mongo_bson)
            dependency(Libs.mongo_driver)
            dependency(Libs.mongo_driver_async)
            dependency(Libs.mongo_driver_core)
            dependency(Libs.mongo_driver_reactivestreams)

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
            dependency(Libs.h2)

            dependency(Libs.cache2k_api)
            dependency(Libs.cache2k_core)
            dependency(Libs.cache2k_jcache)
            dependency(Libs.cache2k_spring)

            dependency(Libs.caffeine)
            dependency(Libs.caffeine_guava)
            dependency(Libs.caffeine_jcache)

            // Koin
            dependency(Libs.koin_core)
            dependency(Libs.koin_core_ext)
            dependency(Libs.koin_test)

            // Metrics
            dependency(Libs.latencyUtils)
            dependency(Libs.hdrHistogram)

            dependency(Libs.objenesis)
            dependency(Libs.ow2_asm)
            dependency(Libs.ow2_asm_commons)
            dependency(Libs.ow2_asm_tree)
            dependency(Libs.ow2_asm_util)

            dependency(Libs.reflectasm)

            dependency(Libs.junit_jupiter)
            dependency(Libs.junit_jupiter_api)
            dependency(Libs.junit_jupiter_engine)
            dependency(Libs.junit_jupiter_params)
            dependency(Libs.junit_jupiter_migrationsupport)

            dependency(Libs.junit_platform_commons)
            dependency(Libs.junit_platform_launcher)
            dependency(Libs.junit_platform_runner)
            dependency(Libs.junit_platform_engine)
            dependency(Libs.junit_platform_suite_api)

            dependency(Libs.kluent)
            dependency(Libs.assertj_core)

            dependency(Libs.mockk)
            dependency(Libs.mockito_core)
            dependency(Libs.mockito_junit_jupiter)
            dependency(Libs.mockito_kotlin)

            dependency(Libs.random_beans)
            dependency(Libs.javafaker)

            dependency(Libs.bouncycastle_bcpkix)
            dependency(Libs.bouncycastle_bcprov)

            dependency(Libs.prometheus_simpleclient)
            dependency(Libs.prometheus_simpleclient_common)
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

        val testRuntimeOnly by configurations

        implementation(Libs.jetbrains_annotations)

        implementation(Libs.kotlin_stdlib)
        implementation(Libs.kotlin_stdlib_common)
        implementation(Libs.kotlin_stdlib_jdk7)
        implementation(Libs.kotlin_stdlib_jdk8)
        implementation(Libs.kotlin_reflect)
        testImplementation(Libs.kotlin_test)
        testImplementation(Libs.kotlin_test_junit5)

        implementation(Libs.atomicfu)

        api(Libs.slf4j_api)
        testApi(Libs.logback)

        testImplementation(Libs.junit_jupiter)
        testImplementation(Libs.junit_jupiter_migrationsupport)
        testRuntimeOnly(Libs.junit_platform_engine)

        testImplementation(Libs.kluent)
        testImplementation(Libs.mockk)
        testImplementation(Libs.testcontainers)
        // testcontainers 가 M1 에서 docker가 안될 때
        // https://github.com/testcontainers/testcontainers-java/issues/3834
        testImplementation(Libs.jna)

        // Property baesd test
        testImplementation(Libs.javafaker)
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
                        name.set("Kommons")
                        description.set("Kotlin JVM Common Libs")
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
                            url.set("https://www.github.com/debop/kommons")
                            connection.set("scm:git:https://www.github.com/debop/kommons")
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
