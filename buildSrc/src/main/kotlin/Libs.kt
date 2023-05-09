import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object Plugins {

    object Versions {
        const val dokka = "1.7.20"
        const val detekt = "1.21.0"
        const val dependency_management = "1.1.0"
        const val jooq = "3.0.3"
        const val protobuf = "0.8.19"
        const val avro = "1.7.0"
        const val jacoco = "0.8.8"
        const val jarTest = "1.0.1"
        const val testLogger = "3.2.0"
        const val shadow = "7.1.2"
        const val kotlinx_benchmark = "0.4.7"
        const val spring_boot = "2.7.11"
        const val quarkus = "2.16.6.Final"
    }

    const val detekt = "io.gitlab.arturbosch.detekt"
    const val dokka = "org.jetbrains.dokka"
    const val dependency_management = "io.spring.dependency-management"
    const val spring_boot = "org.springframework.boot"

    const val jooq = "nu.studer.jooq"

    // https://github.com/google/protobuf-gradle-plugin
    const val protobuf = "com.google.protobuf"

    // https://github.com/davidmc24/gradle-avro-plugin
    const val avro = "com.github.davidmc24.gradle.plugin.avro" //"com.commercehub.gradle.plugin.avro"

    const val jarTest = "com.github.hauner.jarTest"

    // https://mvnrepository.com/artifact/com.adarshr/gradle-test-logger-plugin
    const val testLogger = "com.adarshr.test-logger"
    const val shadow = "com.github.johnrengelman.shadow"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-benchmark-plugin
    const val kotlinx_benchmark = "org.jetbrains.kotlinx.benchmark"

    // Quarkus
    const val quarkus = "io.quarkus"
}

object Versions {

    const val kotlin = "1.8.21"
    const val kotlinx_coroutines = "1.6.4"
    const val kotlinx_serialization = "1.5.0"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/atomicfu
    const val kotlinx_atomicfu = "0.20.2"

    const val kotlinx_benchmark = Plugins.Versions.kotlinx_benchmark

    const val spring_boot = Plugins.Versions.spring_boot
    const val spring_cloud = "2021.0.6"
    const val reactor_bom = "2022.0.6"
    const val spring_statemachine = "3.2.0"

    const val blockhound = "1.0.8.RELEASE"

    // GraphQL
    // Netflix DGS 의 bom (5.5.x) 에서 graph-java 버전이 낮아서 (18.3) 최신 버전 (19.2)으로 강제 update 해야 한다
    // https://github.com/Netflix/dgs-framework/issues/1281#issuecomment-1284694300
    const val graphql_java = "19.2"
    const val graphql_dgs = "5.5.1"
    const val apollo_kotlin = "3.7.4"

    const val quarkus = Plugins.Versions.quarkus
    const val resteasy = "6.2.3.Final"
    const val mutiny = "2.2.0"
    const val vertx = "4.4.1"
    const val agroal = "1.16"

    const val swagger = "1.6.2"
    const val springdoc_openapi = "1.7.0"
    const val springfox_swagger = "3.0.0"
    const val problem = "0.27.1"

    const val bucket4j = "7.6.0"
    const val resilience4j = "2.0.2"
    const val netty = "4.1.91.Final"

    // https://mvnrepository.com/artifact/com.amazonaws
    const val aws = "1.12.459"

    // https://mvnrepository.com/artifact/software.amazon.awssdk/aws-sdk-java
    const val aws2 = "2.20.56"

    const val grpc = "1.54.1"
    const val grpc_kotlin = "1.3.0"
    const val protobuf = "3.22.3"

    // https://mvnrepository.com/artifact/org.apache.avro/avro
    const val avro = "1.11.1"

    const val feign = "12.3"
    const val httpclient5 = "5.2.1"
    const val retrofit2 = "2.9.0"
    const val okhttp3 = "4.11.0"
    const val asynchttpclient = "2.12.3"

    const val jackson = "2.15.0"
    const val jjwt = "0.11.5"

    const val mapstruct = "1.5.5.Final"
    const val reflectasm = "1.11.9"

    const val mongo_driver = "4.9.1"
    const val lettuce = "6.2.4.RELEASE"

    // 참고: https://github.com/redisson/redisson/issues/4809
    const val redisson = "3.21.0"

    // NOTE: Hibernate 는 jakarta 버전인 경우 orm, validator 등이 group 에 포함됩니다.
    // NOTE: 이 경우 기존 javax 를 사용하는 버전과 충돌이 생길 수 있으니 조심하세요 
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core

    // https://mvnrepository.com/artifact/org.hibernate/hibernate-core
    const val hibernate = "5.6.15.Final"
    const val hibernate_reactive = "1.1.9.Final"

    // https://mvnrepository.com/artifact/org.hibernate/hibernate-validator
    const val hibernate_validator = "8.0.0.Final"
    const val querydsl = "5.0.0"

    // https://mvnrepository.com/artifact/io.quarkus.platform/quarkus-blaze-persistence-bom
    const val blaze_persistence = "1.6.8"

    const val slf4j = "1.7.36"
    const val logback = "1.2.11"
    const val log4j = "2.20.0"

    const val metrics = "4.1.25"
    const val prometheus = "0.16.0"

    //NOTE: spring boot 2.7.x 를 사용할 시 micrometer는 1.9+ 를 사용해야 합니다.
    const val micrometer = "1.10.6"
    const val micrometerTracing = "1.0.4"

    // https://mvnrepository.com/artifact/io.opentelemetry/opentelemetry-bom
    const val opentelemetry = "1.25.0"

    // https://mvnrepository.com/artifact/io.opentelemetry/opentelemetry-bom-alpha
    const val opentelemetryAlpha = "$opentelemetry-alpha"

    // https://mvnrepository.com/artifact/io.opentelemetry.instrumentation/opentelemetry-instrumentation-bom-alpha
    const val opentelemetryInstrumentationAlpha = "$opentelemetry-alpha"

    const val caffeine = "3.1.6"        // Java 9+ 이상에서는 3.x 사용
    const val ehcache = "3.10.8"
    const val cache2k = "2.6.1.Final"

    const val ignite = "2.14.0"
    const val hazelcast = "5.2.3"
    const val hazelcast_client = "3.12.13"

    // https://mvnrepository.com/artifact/com.datastax.oss/java-driver-core
    const val cassandra = "4.15.0"
    const val scylla_java = "4.13.0.0"
    const val elasticsearch = "8.7.0"

    const val kafka = "3.4.0"

    const val eclipse_collections = "11.1.0"
    const val jctools = "3.3.0"

    const val ow2_asm = "9.5"

    const val junit_jupiter = "5.9.3"
    const val junit_platform = "1.9.3"
    const val assertj_core = "3.24.2"
    const val kluent = "1.73"
    const val mockk = "1.13.5"
    const val springmockk = "4.0.2"
    const val mockito = "3.12.4"
    const val awaitility = "4.2.0"
    const val jmh = "1.36"
    const val testcontainers = "1.18.0"
    const val jna = "5.13.0"
    const val archunit = "0.21.0"

    const val datafaker = "1.9.0"
    const val snakeyaml = "1.33"
    const val random_beans = "3.9.0"

    // https://mvnrepository.com/artifact/com.github.maricn/logback-slack-appender
    const val logback_slack_appender = "1.6.1"

    // https://mvnrepository.com/artifact/io.sentry/sentry-logback
    const val sentry_logback = "6.17.0"
}

object Libs {

    fun getOsClassifier(): String {
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        val osName = when {
            os.isMacOsX  -> "osx"
            os.isLinux   -> "linux"
            os.isWindows -> "windows"
            else         -> ""
        }
        if (osName.isEmpty()) {
            return osName
        } else {
            // FIXME: isArm 이 제대로 동작하지 않는다 ㅠ.ㅠ
            val architecture = DefaultNativePlatform.getCurrentArchitecture()
            println("architecture=$architecture")

            val archName = if (architecture.name.startsWith("arm")) "aarch_64" else "x86_64"
            return "$osName-$archName".apply {
                println("classifier=$this")
            }
        }
    }

    val jetbrains_annotations get() = "org.jetbrains:annotations:24.0.1"

    // kotlin
    fun kotlin(module: String, version: String = Versions.kotlin) = "org.jetbrains.kotlin:kotlin-$module:$version"

    val kotlin_bom get() = kotlin("bom")
    val kotlin_stdlib get() = kotlin("stdlib")
    val kotlin_stdlib_common get() = kotlin("stdlib-common")
    val kotlin_stdlib_jdk7 get() = kotlin("stdlib-jdk7")
    val kotlin_stdlib_jdk8 get() = kotlin("stdlib-jdk8")
    val kotlin_reflect get() = kotlin("reflect")
    val kotlin_test get() = kotlin("test")
    val kotlin_test_common get() = kotlin("test-common")
    val kotlin_test_junit5 get() = kotlin("test-junit5")

    // Kotlin 1.3.40 부터는 kotlin-scripting-jsr223 만 참조하면 됩니다.
    val kotlin_scripting_jsr223 get() = kotlin("scripting-jsr223")
    val kotlin_compiler get() = kotlin("compiler")

    // Kotlin 1.4+ 부터는 kotlin-scripting-dependencies 를 참조해야 합니다.
    val kotlin_scripting_dependencies get() = kotlin("scripting-dependencies")

    val kotlin_compiler_embeddable get() = kotlin("compiler-embeddable")
    val kotlin_daemon_client get() = kotlin("daemon-client")
    val kotlin_scripting_common get() = kotlin("scripting-common")
    val kotlin_scripting_compiler_embeddable get() = kotlin("scripting-compiler-embeddable")
    val kotlin_scripting_jvm get() = kotlin("scripting-jvm")
    val kotlin_script_runtime get() = kotlin("script-runtime")
    val kotlin_script_util get() = kotlin("scripting-util")

    fun kotlinxCoroutines(module: String, version: String = Versions.kotlinx_coroutines) =
        "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$version"

    val kotlinx_coroutines_bom get() = kotlinxCoroutines("bom")
    val kotlinx_coroutines_core get() = kotlinxCoroutines("core")
    val kotlinx_coroutines_core_common get() = kotlinxCoroutines("core-common")
    val kotlinx_coroutines_core_jvm get() = kotlinxCoroutines("core-jvm")
    val kotlinx_coroutines_debug get() = kotlinxCoroutines("debug")
    val kotlinx_coroutines_jdk7 get() = kotlinxCoroutines("jdk7")
    val kotlinx_coroutines_jdk8 get() = kotlinxCoroutines("jdk8")
    val kotlinx_coroutines_jdk9 get() = kotlinxCoroutines("jdk9")
    val kotlinx_coroutines_reactive get() = kotlinxCoroutines("reactive")
    val kotlinx_coroutines_reactor get() = kotlinxCoroutines("reactor")
    val kotlinx_coroutines_rx2 get() = kotlinxCoroutines("rx2")
    val kotlinx_coroutines_rx3 get() = kotlinxCoroutines("rx3")
    val kotlinx_coroutines_slf4j get() = kotlinxCoroutines("slf4j")
    val kotlinx_coroutines_test get() = kotlinxCoroutines("test")
    val kotlinx_coroutines_test_jvm get() = kotlinxCoroutines("test-jvm")

    // https://github.com/akarnokd/kotlin-flow-extensions
    val kotlin_flow_extensions get() = "com.github.akarnokd:kotlin-flow-extensions:0.0.14"

    // Coroutines Flow를 Reactor처럼 테스트 할 수 있도록 해줍니다.
    // 참고: https://github.com/cashapp/turbine/
    val turbine get() = "app.cash.turbine:turbine:0.12.1"
    val turbine_jvm get() = "app.cash.turbine:turbine-jvm:0.12.1"

    fun kotlinxSerialization(module: String, version: String = Versions.kotlinx_serialization) =
        "org.jetbrains.kotlinx:kotlinx-serialization-$module:$version"

    val kotlinx_serialization_bom get() = kotlinxSerialization("bom")
    val kotlinx_serialization_json get() = kotlinxSerialization("json")
    val kotlinx_serialization_json_jvm get() = kotlinxSerialization("json-jvm")
    val kotlinx_serialization_properties get() = kotlinxSerialization("properties")
    val kotlinx_serialization_properties_jvm get() = kotlinxSerialization("properties-jvm")
    val kotlinx_serialization_protobuf get() = kotlinxSerialization("protobuf")
    val kotlinx_serialization_protobuf_jvm get() = kotlinxSerialization("protobuf-jvm")

    val kotlinx_atomicfu get() = "org.jetbrains.kotlinx:atomicfu:${Versions.kotlinx_atomicfu}"
    val kotlinx_atomicfu_jvm get() = "org.jetbrains.kotlinx:atomicfu-jvm:${Versions.kotlinx_atomicfu}"

    // https://github.com/Kotlin/kotlinx-benchmark
    fun kotlinxBenchmark(module: String, version: String = Versions.kotlinx_benchmark) =
        "org.jetbrains.kotlinx:kotlinx-benchmark-$module:$version"

    val kotlinx_benchmark_runtime get() = kotlinxBenchmark("runtime")
    val kotlinx_benchmark_runtime_jvm get() = kotlinxBenchmark("runtime-jvm")

    // javax api
    val javax_activation_api get() = "javax.activation:javax.activation-api:1.2.0"
    val javax_annotation_api get() = "javax.annotation:javax.annotation-api:1.3.2"
    val javax_cache_api get() = "javax.cache:cache-api:1.1.1"
    val javax_inject get() = "javax.inject:javax.inject:1"
    val javax_persistence_api get() = "javax.persistence:javax.persistence-api:2.2"
    val javax_servlet_api get() = "javax.servlet:javax.servlet-api:4.0.1"
    val javax_transaction_api get() = "javax.transaction:jta:1.1"
    val javax_validation_api get() = "javax.validation:validation-api:2.0.1.Final"
    val javax_ws_rs_api get() = "javax.ws.rs:javax.ws.rs-api:2.1.1"

    // javax expression
    val javax_el_api get() = "javax.el:javax.el-api:3.0.0"
    val javax_el get() = "org.glassfish:javax.el:3.0.1-b12"

    // json 구현체
    val javax_json_api get() = "javax.json:javax.json-api:1.1.4"
    val javax_json get() = "org.glassfish:javax.json:1.1.4"

    // Java Money
    val javax_money_api get() = "javax.money:money-api:1.1"
    val javamoney_moneta get() = "org.javamoney:moneta:1.4.2"

    // jakarta
    val jakarta_activation_api get() = "jakarta.activation:jakarta.activation-api:2.1.1"
    val jakarta_annotation_api get() = "jakarta.annotation:jakarta.annotation-api:2.1.1"
    val jakarta_el_api get() = "jakarta.el:jakarta.el-api:5.0.1"
    val jakarta_el get() = "org.glassfish:jakarta.el:5.0.0-M1"
    val jakarta_inject_api get() = "jakarta.inject:jakarta.inject-api:1.0.5"
    val jakarta_interceptor_api get() = "jakarta.interceptor:jakarta.interceptor-api:2.1.0"
    val jakarta_jms_api get() = "jakarta.jms:jakarta.jms-api:3.1.0"
    val jakarta_json_api get() = "jakarta.json:jakarta.json-api:1.1.6"
    val jakarta_json get() = "org.glassfish:jakarta.json:2.0.1"
    val jakarta_persistence_api get() = "jakarta.persistence:jakarta.persistence-api:3.1.0"
    val jakarta_servlet_api get() = "jakarta.servlet:jakarta.servlet-api:6.0.0"
    val jakarta_transaction_api get() = "jakarta.transaction:jakarta.transaction-api:2.0.1"
    val jakarta_validation_api get() = "jakarta.validation:jakarta.validation-api:3.0.2"
    val jakarta_ws_rs_api get() = "jakarta.ws.rs:jakarta.ws.rs-api:3.1.0"


    // Apache Commons
    val commons_beanutils get() = "commons-beanutils:commons-beanutils:1.9.4"
    val commons_compress get() = "org.apache.commons:commons-compress:1.21"
    val commons_codec get() = "commons-codec:commons-codec:1.15"
    val commons_collections4 get() = "org.apache.commons:commons-collections4:4.4"
    val commons_cryto get() = "org.apache.commons:commons-crypto:1.2.0"
    val commons_csv get() = "org.apache.commons:commons-csv:1.9.0"
    val commons_digest3 get() = "org.apache.commons:commons-digester3:3.2"
    val commons_exec get() = "org.apache.commons:commons-exec:1.3"
    val commons_io get() = "commons-io:commons-io:2.11.0"
    val commons_lang3 get() = "org.apache.commons:commons-lang3:3.12.0"
    val commons_logging get() = "commons-logging:commons-logging:1.2"
    val commons_math3 get() = "org.apache.commons:commons-math3:3.6.1"
    val commons_pool2 get() = "org.apache.commons:commons-pool2:2.11.1"
    val commons_rng_simple get() = "org.apache.commons:commons-rng-simple:1.4"
    val commons_text get() = "org.apache.commons:commons-text:1.9"
    val commons_validator get() = "commons-validator:commons-validator:1.7"

    val slf4j_api get() = "org.slf4j:slf4j-api:${Versions.slf4j}"
    val slf4j_simple get() = "org.slf4j:slf4j-simple:${Versions.slf4j}"
    val slf4j_log4j12 get() = "org.slf4j:slf4j-log4j12:${Versions.slf4j}"
    val jcl_over_slf4j get() = "org.slf4j:jcl-over-slf4j:${Versions.slf4j}"
    val jul_to_slf4j get() = "org.slf4j:jul-to-slf4j:${Versions.slf4j}"
    val log4j_over_slf4j get() = "org.slf4j:log4j-over-slf4j:${Versions.slf4j}"

    val logback get() = "ch.qos.logback:logback-classic:${Versions.logback}"
    val logback_core get() = "ch.qos.logback:logback-core:${Versions.logback}"

    fun log4j(module: String) = "org.apache.logging.log4j:log4j-$module:${Versions.log4j}"
    val log4j_bom get() = log4j("bom")
    val log4j_api get() = log4j("api")
    val log4j_core get() = log4j("core")
    val log4j_jcl get() = log4j("jcl")
    val log4j_jul get() = log4j("jul")
    val log4j_slf4j_impl get() = log4j("slf4j-impl")
    val log4j_web get() = log4j("web")

    val findbugs get() = "com.google.code.findbugs:jsr305:3.0.2"
    val guava get() = "com.google.guava:guava:31.1-jre"

    val eclipse_collections get() = "org.eclipse.collections:eclipse-collections:${Versions.eclipse_collections}"
    val eclipse_collections_forkjoin get() = "org.eclipse.collections:eclipse-collections-forkjoin:${Versions.eclipse_collections}"
    val eclipse_collections_testutils get() = "org.eclipse.collections:eclipse-collections-testutils:${Versions.eclipse_collections}"

    // https://github.com/JCTools/JCTools
    val jctools_core = "org.jctools:jctools-core:${Versions.jctools}"

    val kryo get() = "com.esotericsoftware:kryo:5.5.0"
    val marshalling get() = "org.jboss.marshalling:jboss-marshalling:2.1.1.Final"
    val marshalling_river get() = "org.jboss.marshalling:jboss-marshalling-river:2.1.1.Final"
    val marshalling_serial get() = "org.jboss.marshalling:jboss-marshalling-serial:2.1.1.Final"

    // Spring Boot
    val spring_boot_dependencies
        get() = "org.springframework.boot:spring-boot-dependencies:${Versions.spring_boot}"

    fun spring(module: String) = "org.springframework:spring-$module"
    fun springBoot(module: String) = "org.springframework.boot:spring-boot-$module"
    fun springBootStarter(module: String) = "org.springframework.boot:spring-boot-starter-$module"
    fun springData(module: String) = "org.springframework.data:spring-data-$module"
    fun springSecurity(module: String) = "org.springframework.security:spring-security-$module"

    // Spring Cloud
    val spring_cloud_dependencies
        get() = "org.springframework.cloud:spring-cloud-dependencies:${Versions.spring_cloud}"

    fun springCloud(module: String) = "org.springframework.cloud:spring-cloud-$module"
    fun springCloudStarter(module: String) = "org.springframework.cloud:spring-cloud-starter-$module"

    val spring_cloud_commons get() = springCloud("commons")
    val spring_cloud_stream get() = springCloud("stream")
    val spring_cloud_starter_bootstrap get() = springCloudStarter("bootstrap")

    fun springStatemachine(module: String) =
        "org.springframework.statemachine:spring-statemachine-$module:${Versions.spring_statemachine}"

    val spring_statemachine_bom get() = springStatemachine("bom")
    val spring_statemachine_core get() = springStatemachine("core")

    // GraphQL JAVA
    fun graphqlJava(module: String) = "com.graphql-java:$module:${Versions.graphql_java}"
    val graphql_java get() = graphqlJava("graphql-java")

    // GraphQL DGS
    fun graphqlDgs(module: String) = "com.netflix.graphql.dgs:graphql-dgs-$module:${Versions.graphql_dgs}"

    val graphql_dgs_platform_dependencies get() = graphqlDgs("platform-dependencies")
    val graphql_dgs_client get() = graphqlDgs("client")
    val graphql_dgs_extended_shaded get() = graphqlDgs("extended-shaded")
    val graphql_dgs_extended_scalars get() = graphqlDgs("extended-scalars")
    val graphql_dgs_extended_validation get() = graphqlDgs("extended-validation")
    val graphql_dgs_mocking get() = graphqlDgs("mocking")
    val graphql_dgs_pagination get() = graphqlDgs("pagination")
    val graphql_dgs_reactive get() = graphqlDgs("reactive")
    val graphql_dgs_spring_boot_starter get() = graphqlDgs("spring-boot-starter")
    val graphql_dgs_spring_webmvc get() = graphqlDgs("spring-webmvc")
    val graphql_dgs_spring_webmvc_autoconfigure get() = graphqlDgs("spring-webmvc-autoconfigure")
    val graphql_dgs_subscriptions_sse get() = graphqlDgs("subscriptions-sse")
    val graphql_dgs_subscriptions_sse_autoconfigure get() = graphqlDgs("subscriptions-sse-autoconfigure")
    val graphql_dgs_subscriptions_websockets get() = graphqlDgs("subscriptions-websockets")
    val graphql_dgs_subscriptions_websockets_autoconfigure get() = graphqlDgs("subscriptions-websockets-autoconfigure")
    val graphql_dgs_webflux_starter get() = graphqlDgs("webflux-starter")
    val graphql_dgs_error_types get() = "com.netflix.graphql.dgs:graphql-error-types:${Versions.graphql_dgs}"

    // Apollo Kotlin
    fun apolloKotlin(module: String) = "com.apollographql.apollo3:apollo-$module:${Versions.apollo_kotlin}"
    val apollo_adapters get() = apolloKotlin("adapters")
    val apollo_mockserver get() = apolloKotlin("mockserver")
    val apollo_normalized_cache get() = apolloKotlin("normalized-cache")
    val apollo_runtime get() = apolloKotlin("runtime")
    val apollo_runtime_jvm get() = apolloKotlin("runtime-jvm")
    val apollo_testing_support get() = apolloKotlin("testing-support")

    // Apollo Federation (DGS bom에 정의되어 있다)
    fun apolloFederation(module: String) = "com.apollographql.federation:federation-$module"
    val apollo_federation_graphql_java_support get() = apolloFederation("graphql-java-support")


    // Quarkus
    fun quarkus(extension: String) = "io.quarkus:quarkus-$extension:${Versions.quarkus}"

    val quarkus_bom get() = "io.quarkus.platform:quarkus-bom:${Versions.quarkus}"

    // rest-assured
    fun restAssured(module: String) = "io.rest-assured:$module"
    val rest_assured get() = restAssured("rest-assured")
    val rest_assured_kotlin get() = restAssured("kotlin-extensions")

    // Vert.x (https://vertx.io/docs/)
    fun vertx(module: String, version: String = Versions.vertx) = "io.vertx:vertx-$module:$version"
    val vertx_dependencies get() = vertx("dependencies")

    val vertx_core get() = vertx("core")
    val vertx_codegen get() = vertx("codegen") + ":processor"
    val vertx_lang_kotlin get() = vertx("lang-kotlin")
    val vertx_lang_kotlin_coroutines get() = vertx("lang-kotlin-coroutines")
    val vertx_jdbc_client get() = vertx("jdbc-client")
    val vertx_sql_client get() = vertx("sql-client")
    val vertx_sql_client_templates get() = vertx("sql-client-templates")
    val vertx_mysql_client get() = vertx("mysql-client")
    val vertx_pg_client get() = vertx("pg-client")
    val vertx_web get() = vertx("web")
    val vertx_web_client get() = vertx("web-client")
    val vertx_junit5 get() = vertx("junit5")

    // Resteasy (https://resteasy.dev/)
    fun resteasy(module: String, version: String = Versions.resteasy) = "org.jboss.resteasy:resteasy-$module:$version"
    val resteasy_bom get() = resteasy("bom")

    val resteasy_cdi get() = resteasy("cdi")
    val resteasy_client get() = resteasy("client")
    val resteasy_jackson2_provider get() = resteasy("jackson2-provider")
    val resteasy_spring get() = resteasy("spring")
    val resteasy_vertx get() = resteasy("vertx")

    // Agroal Data for Vertx
    fun agroal(module: String, version: String = Versions.agroal) = "io.agroal:agroal-$module:$version"
    val agroal_pool get() = agroal("pool")
    val agroal_narayana get() = agroal("narayana")
    val agroal_hikari get() = agroal("hikari")
    val agroal_spring_boot_starter get() = agroal("spring-boot-starter")

    // Resilience4j
    fun resilience4j(module: String, version: String = Versions.resilience4j) =
        "io.github.resilience4j:resilience4j-$module:$version"

    // resilience4j-bom 은 1.7.1 로 update 되지 않았다 (배포 실수인 듯)
    val resilience4j_bom get() = resilience4j("bom")
    val resilience4j_all get() = resilience4j("all")
    val resilience4j_annotations get() = resilience4j("annotations")
    val resilience4j_bulkhead get() = resilience4j("bulkhead")
    val resilience4j_cache get() = resilience4j("cache")
    val resilience4j_circuitbreaker get() = resilience4j("circuitbreaker")
    val resilience4j_circularbuffer get() = resilience4j("circularbuffer")
    val resilience4j_consumer get() = resilience4j("consumer")
    val resilience4j_core get() = resilience4j("core")
    val resilience4j_feign get() = resilience4j("feign")
    val resilience4j_framework_common get() = resilience4j("framework-common")
    val resilience4j_kotlin get() = resilience4j("kotlin")
    val resilience4j_metrics get() = resilience4j("metrics")
    val resilience4j_micrometer get() = resilience4j("micrometer")
    val resilience4j_prometheus get() = resilience4j("prometheus")
    val resilience4j_ratelimiter get() = resilience4j("ratelimiter")
    val resilience4j_ratpack get() = resilience4j("ratpack")
    val resilience4j_reactor get() = resilience4j("reactor")
    val resilience4j_retrofit get() = resilience4j("retrofit")
    val resilience4j_retry get() = resilience4j("retry")
    val resilience4j_rxjava2 get() = resilience4j("rxjava2")
    val resilience4j_rxjava3 get() = resilience4j("rxjava3")
    val resilience4j_spring get() = resilience4j("spring")
    val resilience4j_spring_boot2 get() = resilience4j("spring-boot2")
    val resilience4j_spring_cloud2 get() = resilience4j("spring-cloud2")
    val resilience4j_timelimiter get() = resilience4j("timelimiter")
    val resilience4j_vertx get() = resilience4j("vertx")

    // Bucket4j
    fun bucket4j(module: String) = "com.github.vladimir-bukhtoyarov:bucket4j-$module:${Versions.bucket4j}"
    val bucket4j_core get() = bucket4j("core")
    val bucket4j_jcache get() = bucket4j("jcache")
    val bucket4j_ignite get() = bucket4j("ignite")

    // Netty
    fun netty(module: String, version: String = Versions.netty) = "io.netty:netty-$module:$version"
    val netty_bom get() = netty("bom")
    val netty_all get() = netty("all")
    val netty_common get() = netty("common")
    val netty_buffer get() = netty("buffer")
    val netty_codec get() = netty("codec")
    val netty_codec_dns get() = netty("codec-dns")
    val netty_codec_http get() = netty("codec-http")
    val netty_codec_http2 get() = netty("codec-http2")
    val netty_codec_socks get() = netty("codec-socks")
    val netty_handler get() = netty("handler")
    val netty_handler_proxy get() = netty("handler-proxy")
    val netty_resolver get() = netty("resolver")
    val netty_resolver_dns get() = netty("resolver-dns")
    val netty_resolver_dns_native_macos get() = netty("resolver-dns-native-macos")
    val netty_transport get() = netty("transport")
    val netty_transport_native_epoll get() = netty("transport-native-epoll")
    val netty_transport_native_kqueue get() = netty("transport-native-kqueue")

    // gRPC
    fun grpc(module: String) = "io.grpc:grpc-$module:${Versions.grpc}"
    val grpc_bom get() = grpc("bom")
    val grpc_alts get() = grpc("alts")
    val grpc_api get() = grpc("api")
    val grpc_auth get() = grpc("auth")
    val grpc_context get() = grpc("context")
    val grpc_core get() = grpc("core")
    val grpc_grpclb get() = grpc("grpclb")
    val grpc_protobuf get() = grpc("protobuf")
    val grpc_protobuf_lite get() = grpc("protobuf-lite")
    val grpc_stub get() = grpc("stub")
    val grpc_services get() = grpc("services")
    val grpc_netty get() = grpc("netty")
    val grpc_netty_shaded get() = grpc("netty-shaded")
    val grpc_okhttp get() = grpc("okhttp")
    val grpc_testing get() = grpc("testing")

    val grpc_protoc_gen_grpc_java get() = "io.grpc:protoc-gen-grpc-java:${Versions.grpc}"

    // gRPC Kotlin
    val grpc_kotlin_stub get() = "io.grpc:grpc-kotlin-stub:${Versions.grpc_kotlin}"
    val grpc_protoc_gen_grpc_kotlin get() = "io.grpc:protoc-gen-grpc-kotlin:${Versions.grpc_kotlin}:jdk8@jar"

    val protobuf_bom get() = "com.google.protobuf:protobuf-bom:${Versions.protobuf}"
    val protobuf_protoc get() = "com.google.protobuf:protoc:${Versions.protobuf}"
    val protobuf_java get() = "com.google.protobuf:protobuf-java:${Versions.protobuf}"
    val protobuf_java_util get() = "com.google.protobuf:protobuf-java-util:${Versions.protobuf}"
    val protobuf_kotlin get() = "com.google.protobuf:protobuf-kotlin:${Versions.protobuf}"

    val avro get() = "org.apache.avro:avro:${Versions.avro}"
    val avro_ipc get() = "org.apache.avro:avro-ipc:${Versions.avro}"
    val avro_ipc_netty get() = "org.apache.avro:avro-ipc-netty:${Versions.avro}"
    val avro_compiler get() = "org.apache.avro:avro-compiler:${Versions.avro}"
    val avro_protobuf get() = "org.apache.avro:avro-protobuf:${Versions.avro}"

    // https://mvnrepository.com/artifact/com.github.avro-kotlin.avro4k/avro4k-core
    val avro_kotlin get() = "com.github.avro-kotlin.avro4k:avro4k-core:1.8.0"

    fun awsSdk(name: String, version: String = Versions.aws) = "com.amazonaws:aws-java-sdk-$name:$version"
    val aws_bom get() = awsSdk("bom")
    val aws_java_sdk_s3 get() = awsSdk("s3")
    val aws_java_sdk_dynamodb get() = awsSdk("dynamodb")
    val aws_java_sdk_ses get() = awsSdk("ses")
    val aws_java_sdk_sns get() = awsSdk("sns")
    val aws_java_sdk_sqs get() = awsSdk("sqs")
    val aws_java_sdk_sts get() = awsSdk("sts")
    val aws_java_sdk_ec2 get() = awsSdk("ec2")
    val aws_java_sdk_test_utils get() = awsSdk("test-utils")

    fun awsSdkV2(name: String, version: String = Versions.aws2) = "software.amazon.awssdk:$name:$version"
    val aws2_bom get() = awsSdkV2("bom")
    val aws2_applicationautoscaling get() = awsSdkV2("applicationautoscaling")
    val aws2_auth get() = awsSdkV2("auth")
    val aws2_aws_core get() = awsSdkV2("aws-core")
    val aws2_sdk_core get() = awsSdkV2("sdk-core")
    val aws2_cloudwatch get() = awsSdkV2("cloudwatch")
    val aws2_cloudwatchevents get() = awsSdkV2("cloudwatchevents")
    val aws2_cloudwatchlogs get() = awsSdkV2("cloudwatchlogs")
    val aws2_ec2 get() = awsSdkV2("ec2")
    val aws2_elasticache get() = awsSdkV2("elasticache")
    val aws2_kafka get() = awsSdkV2("kafka")
    val aws2_kms get() = awsSdkV2("kms")
    val aws2_lambda get() = awsSdkV2("lambda")
    val aws2_s3 get() = awsSdkV2("s3")
    val aws2_s3_transfer_manager get() = awsSdkV2("s3-transfer-manager")
    val aws2_ses get() = awsSdkV2("ses")
    val aws2_sqs get() = awsSdkV2("sqs")
    val aws2_sts get() = awsSdkV2("sts")
    val aws2_dynamodb_enhanced get() = awsSdkV2("dynamodb-enhanced")
    val aws2_utils get() = awsSdkV2("utils")
    val aws2_test_utils get() = awsSdkV2("test-utils")

    // https://docs.aws.amazon.com/ko_kr/sdk-for-java/latest/developer-guide/http-configuration-crt.html
    // https://mvnrepository.com/artifact/software.amazon.awssdk.crt/aws-crt
    val aws2_aws_crt get() = "software.amazon.awssdk.crt:aws-crt:0.21.14"


    // AsyncHttpClient
    val async_http_client get() = "org.asynchttpclient:async-http-client:${Versions.asynchttpclient}"
    val async_http_client_extras_retrofit2 get() = "org.asynchttpclient:async-http-client-extras-retrofit2:${Versions.asynchttpclient}"
    val async_http_client_extras_rxjava2 get() = "org.asynchttpclient:async-http-client-extras-rxjava2:${Versions.asynchttpclient}"

    // Apache HttpComponents
    val httpclient5 get() = "org.apache.httpcomponents.client5:httpclient5:${Versions.httpclient5}"
    val httpclient5_cache get() = "org.apache.httpcomponents.client5:httpclient5-cache:${Versions.httpclient5}"
    val httpclient5_fluent get() = "org.apache.httpcomponents.client5:httpclient5-fluent:${Versions.httpclient5}"
    val httpcore5 get() = "org.apache.httpcomponents.core5:httpcore5:${Versions.httpclient5}"
    val httpcore5_h2 get() = "org.apache.httpcomponents.core5:httpcore5-h2:${Versions.httpclient5}"
    val httpcore5_reactive get() = "org.apache.httpcomponents.core5:httpcore5-reactive:${Versions.httpclient5}"

    // OpenFeign
    fun feign(module: String) = "io.github.openfeign:feign-$module:${Versions.feign}"
    val feign_bom get() = feign("bom")
    val feign_core get() = feign("core")
    val feign_gson get() = feign("gson")
    val feign_hc5 get() = feign("hc5")
    val feign_httpclient get() = feign("httpclient")
    val feign_jackson get() = feign("jackson")
    val feign_java11 get() = feign("java11")
    val feign_jaxb get() = feign("jaxb")
    val feign_jaxrs get() = feign("jaxrs")
    val feign_jaxrs2 get() = feign("jaxrs2")
    val feign_kotlin get() = feign("kotlin")
    val feign_micrometer get() = feign("micrometer")
    val feign_mock get() = feign("mock")
    val feign_okhttp get() = feign("okhttp")
    val feign_ribbon get() = feign("ribbon")
    val feign_slf4j get() = feign("slf4j")

    // Retrofit2
    fun retrofit2(module: String) = "com.squareup.retrofit2:$module:${Versions.retrofit2}"
    val retrofit2 get() = retrofit2("retrofit")
    val retrofit2_adapter_java8 get() = retrofit2("adapter-java8")
    val retrofit2_adapter_rxjava2 get() = retrofit2("adapter-rxjava2")
    val retrofit2_adapter_rxjava3 get() = retrofit2("adapter-rxjava3")
    val retrofit2_converter_jackson get() = retrofit2("converter-jackson")
    val retrofit2_converter_moshi get() = retrofit2("converter-moshi")
    val retrofit2_converter_protobuf get() = retrofit2("converter-protobuf")
    val retrofit2_converter_scalars get() = retrofit2("converter-scalars")
    val retrofit2_mock get() = retrofit2("retrofit-mock")

    // https://github.com/JakeWharton/retrofit2-reactor-adapter/
    val retrofit2_adapter_reactor get() = "com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0"

    // OkHttp3
    fun okhttp(module: String) = "com.squareup.okhttp3:$module:${Versions.okhttp3}"
    val okhttp3_bom get() = okhttp("okhttp-bom")
    val okhttp3 get() = okhttp("okhttp")
    val okhttp3_logging_interceptor get() = okhttp("logging-interceptor")
    val okhttp3_mockwebserver get() = okhttp("mockwebserver")
    val okhttp3_sse get() = okhttp("okhttp-sse")
    val okhttp3_urlconnection get() = okhttp("okhttp-urlconnection")
    val okhttp3_ws get() = okhttp("okhttp-ws")

    // MapStruct
    val mapstruct get() = "org.mapstruct:mapstruct:${Versions.mapstruct}"
    val mapstruct_processor get() = "org.mapstruct:mapstruct-processor:${Versions.mapstruct}"

    // Jackson
    val jackson_bom get() = "com.fasterxml.jackson:jackson-bom:${Versions.jackson}"

    fun jackson(group: String, module: String, version: String = Versions.jackson): String {
        return if (group == "core") "com.fasterxml.jackson.$group:jackson-$module:$version"
        else "com.fasterxml.jackson.$group:jackson-$group-$module:$version"
    }

    fun jacksonCore(module: String, version: String = Versions.jackson) = jackson("core", module, version)

    val jackson_annotations get() = jacksonCore("annotations")
    val jackson_core get() = jacksonCore("core")
    val jackson_databind get() = jacksonCore("databind")

    fun jacksonDataType(module: String, version: String = Versions.jackson) = jackson("datatype", module, version)
    val jackson_datatype_jsr310 get() = jacksonDataType("jsr310")
    val jackson_datatype_jsr353 get() = jacksonDataType("jsr353")
    val jackson_datatype_jdk8 get() = jacksonDataType("jdk8")
    val jackson_datatype_joda get() = jacksonDataType("joda")
    val jackson_datatype_guava get() = jacksonDataType("guava")

    fun jacksonDataFormat(module: String, version: String = Versions.jackson) = jackson("dataformat", module, version)
    val jackson_dataformat_avro get() = jacksonDataFormat("avro")
    val jackson_dataformat_protobuf get() = jacksonDataFormat("protobuf")
    val jackson_dataformat_csv get() = jacksonDataFormat("csv")
    val jackson_dataformat_properties get() = jacksonDataFormat("properties")
    val jackson_dataformat_yaml get() = jacksonDataFormat("yaml")

    fun jacksonModule(module: String, version: String = Versions.jackson) = jackson("module", module, version)
    val jackson_module_kotlin get() = jacksonModule("kotlin")
    val jackson_module_paranamer get() = jacksonModule("parameter")
    val jackson_module_parameter_names get() = jacksonModule("parameter-names")
    val jackson_module_afterburner get() = jacksonModule("afterburner")

    // Json assertions
    val jsonpath get() = "com.jayway.jsonpath:json-path:2.7.0"
    val jsonassert get() = "org.skyscreamer:jsonassert:1.5.0"

    // GSON
    val gson get() = "com.google.code.gson:gson:2.10.1"
    val gson_javatime_serializers
        get() = "com.fatboyindustrial.gson-javatime-serialisers:gson-javatime-serialisers:1.1.2"

    // JWT
    fun jjwt(module: String) = "io.jsonwebtoken:jjwt-$module:${Versions.jjwt}"
    val jjwt_api get() = jjwt("api")
    val jjwt_impl get() = jjwt("impl")
    val jjwt_jackson get() = jjwt("jackson")
    val jjwt_extensions get() = jjwt("extensions")

    // Compression
    val snappy_java get() = "org.xerial.snappy:snappy-java:1.1.8.4"
    val lz4_java get() = "org.lz4:lz4-java:1.8.0"

    // https://github.com/hyperxpro/Brotli4j
    val brotli4j get() = "com.aayushatharva.brotli4j:brotli4j:1.7.1"
    val brotli4j_native
        get() = "com.aayushatharva.brotli4j:native-${
            getOsClassifier().replace(
                "aarch_64",
                "aarch64"
            )
        }:1.7.1"
    val zstd_jni get() = "com.github.luben:zstd-jni:1.5.2-2"
    val xz get() = "org.tukaani:xz:1.9"

    // Cryptography
    val jasypt get() = "org.jasypt:jasypt:1.9.3"
    val bouncycastle_bcprov get() = "org.bouncycastle:bcprov-jdk15on:1.70"
    val bouncycastle_bcpkix get() = "org.bouncycastle:bcpkix-jdk15on:1.70"

    // MVEL
    val mvel2 get() = "org.mvel:mvel2:2.4.12.Final"

    // Reactor
    val reactor_bom get() = "io.projectreactor:reactor-bom:${Versions.reactor_bom}"
    val reactor_core get() = "io.projectreactor:reactor-core"
    val reactor_adapter get() = "io.projectreactor.addons:reactor-adapter"
    val reactor_extra get() = "io.projectreactor.addons:reactor-extra"
    val reactor_kafka get() = "io.projectreactor.kafka:reactor-kafka"
    val reactor_netty get() = "io.projectreactor.netty:reactor-netty"
    val reactor_test get() = "io.projectreactor:reactor-test"
    val reactor_kotlin_extensions get() = "io.projectreactor.kotlin:reactor-kotlin-extensions"

    val blockhound get() = "io.projectreactor.tools:blockhound:${Versions.blockhound}"
    val blockhound_junit_platform get() = "io.projectreactor.tools:blockhound-junit-platform:${Versions.blockhound}"

    // Smallrye Mutiny
    val mutiny get() = "io.smallrye.reactive:mutiny:${Versions.mutiny}"
    val mutiny_kotlin get() = "io.smallrye.reactive:mutiny-kotlin:${Versions.mutiny}"

    // Metrics
    fun metrics(module: String) = "io.dropwizard.metrics:metrics-$module:${Versions.metrics}"

    val metrics_bom get() = metrics("bom")
    val metrics_annotation get() = metrics("annotation")
    val metrics_core get() = metrics("core")
    val metrics_json get() = metrics("json")
    val metrics_jvm get() = metrics("jvm")
    val metrics_graphite get() = metrics("graphite")
    val metrics_healthchecks get() = metrics("healthchecks")
    val metrics_jcache get() = metrics("jcache")
    val metrics_jmx get() = metrics("jmx")

    // Prometheus
    fun prometheusSimple(module: String) = "io.prometheus:simpleclient_$module:${Versions.prometheus}"
    val prometheus_simpleclient get() = "io.prometheus:simpleclient:${Versions.prometheus}"
    val prometheus_simpleclient_common get() = prometheusSimple("common")
    val prometheus_simpleclient_dropwizard get() = prometheusSimple("dropwizard")
    val prometheus_simpleclient_httpserver get() = prometheusSimple("httpserver")
    val prometheus_simpleclient_pushgateway get() = prometheusSimple("pushgateway")
    val prometheus_simpleclient_spring_boot get() = prometheusSimple("spring_boot")

    val prometheus_simpleclient_tracer_common get() = prometheusSimple("tracer_common")
    val prometheus_simpleclient_tracer_otel get() = prometheusSimple("tracer_otel")
    val prometheus_simpleclient_tracer_otel_agent get() = prometheusSimple("tracer_otel_agent")

    // Micrometer
    fun micrometer(module: String) = "io.micrometer:micrometer-$module:${Versions.micrometer}"
    val micrometer_bom get() = micrometer("bom")
    val micrometer_core get() = micrometer("core")
    val micrometer_test get() = micrometer("test")
    val micrometer_registry_cloudwatch get() = micrometer("registry-cloudwatch")
    val micrometer_registry_elastic get() = micrometer("registry-elastic")
    val micrometer_registry_graphite get() = micrometer("registry-graphite")
    val micrometer_registry_new_relic get() = micrometer("registry-new-relic")
    val micrometer_registry_prometheus get() = micrometer("registry-prometheus")
    val micrometer_registry_jmx get() = micrometer("registry-jmx")

    // Micrometer Tracing
    fun micrometerTracing(module: String) = "io.micrometer:micrometer-tracing-$module:${Versions.micrometerTracing}"
    val micrometer_tracing_bom get() = micrometerTracing("bom")
    val micrometer_tracing_bridge_brave get() = micrometerTracing("bridge-brave")
    val micrometer_tracing_bridge_otel get() = micrometerTracing("bridge-otel")
    val micrometer_tracing_integeration_test get() = micrometerTracing("integration-test")
    val micrometer_tracing_test get() = micrometerTracing("test")


    // OpenTelemetry
    fun opentelemetry(module: String): String = "io.opentelemetry:opentelemetry-$module"

    fun opentelemetryInstrumentation(module: String): String = "io.opentelemetry.instrumentation:opentelemetry-$module"

    val opentelemetry_bom get() = opentelemetry("bom:${Versions.opentelemetry}")
    val opentelemetry_alpha_bom get() = opentelemetry("bom-alpha:${Versions.opentelemetryAlpha}")
    val opentelemetry_instrumentation_bom_alpha get() = opentelemetryInstrumentation("instrumentation-bom-alpha:${Versions.opentelemetryInstrumentationAlpha}")

    val opentelemetry_javaagent_remote_path =
        "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${Versions.opentelemetry}/opentelemetry-javaagent.jar"
    val opentelemetry_javaagent_local_path = "otel/opentelemetry-javaagent.jar"

    val opentelemetry_api get() = opentelemetry("api")
    val opentelemetry_extensions_aws get() = opentelemetry("extension-aws")
    val opentelemetry_extensions_kotlin get() = opentelemetry("extension-kotlin")

    val opentelemetry_exporter_logging get() = opentelemetry("exporter-logging")
    val opentelemetry_exporter_otlp get() = opentelemetry("exporter-otlp")
    val opentelemetry_exporter_otlp_metrics get() = opentelemetry("exporter-otlp-metrics")
    val opentelemetry_exporter_otlp_trace get() = opentelemetry("exporter-otlp-trace")
    val opentelemetry_exporter_otlp_http_metrics get() = opentelemetry("exporter-otlp-http-metrics")
    val opentelemetry_exporter_otlp_http_trace get() = opentelemetry("exporter-otlp-http-trace")
    val opentelemetry_exporter_prometheus get() = opentelemetry("exporter-prometheus")

    val opentelemetry_sdk get() = opentelemetry("sdk")
    val opentelemetry_sdk_metrics get() = opentelemetry("sdk-metrics")
    val opentelemetry_sdk_trace get() = opentelemetry("sdk-trace")
    val opentelemetry_sdk_testing get() = opentelemetry("sdk-testing")
    val opentelemetry_sdk_extensions_aws get() = opentelemetry("sdk-extension-aws")
    val opentelemetry_sdk_extensions_resources get() = opentelemetry("sdk-extension-resources")
    val opentelemetry_sdk_extensions_autoconfigure get() = opentelemetry("sdk-extension-autoconfigure")

    // Opentelemetry Instrumentation
    //
    // https://mvnrepository.com/artifact/io.opentelemetry.instrumentation/opentelemetry-logback-appender-1.0
    val opentelemetry_logback_appender_1_0 get() = opentelemetryInstrumentation("logback-appender-1.0")

    // https://mvnrepository.com/artifact/io.opentelemetry.instrumentation/opentelemetry-logback-mdc-1.0
    val opentelemetry_logback_mdc_1_0 get() = opentelemetryInstrumentation("logback-mdc-1.0")

    val latencyUtils get() = "org.latencyutils:LatencyUtils:2.0.3"
    val hdrHistogram get() = "org.hdrhistogram:HdrHistogram:2.1.11"

    val reflectasm get() = "com.esotericsoftware:reflectasm:${Versions.reflectasm}"

    // mongodb 4.x
    fun mongo(module: String, version: String = Versions.mongo_driver) = "org.mongodb:$module:$version"
    val mongo_bson get() = mongo("bson")
    val mongodb_driver_core get() = mongo("mongodb-driver-core")
    val mongodb_driver_sync get() = mongo("mongodb-driver-sync")
    val mongodb_driver_reactivestreams get() = mongo("mongodb-driver-reactivestreams")
    val mongodb_driver_legacy get() = mongo("mongodb-driver-legacy")

    // Redis
    val lettuce_core get() = "io.lettuce:lettuce-core:${Versions.lettuce}"

    fun redisson(module: String, version: String = Versions.redisson) = "org.redisson:$module:$version"
    val redisson get() = redisson("redisson")
    val redisson_spring_boot_starter get() = redisson("redisson-spring-boot-starter")
    val redisson_spring_data_21 get() = redisson("redisson-spring-data-21")
    val redisson_spring_data_22 get() = redisson("redisson-spring-data-22")
    val redisson_spring_data_23 get() = redisson("redisson-spring-data-23")
    val redisson_spring_data_24 get() = redisson("redisson-spring-data-24")
    val redisson_spring_data_25 get() = redisson("redisson-spring-data-25")
    val redisson_spring_data_26 get() = redisson("redisson-spring-data-26")

    // Memcached
    val folsom get() = "com.spotify:folsom:1.7.4"
    val spymemcached get() = "net.spy:spymemcached:2.12.3"

    // Cassandra
    fun cassandra(module: String, version: String = Versions.cassandra) =
        "com.datastax.oss:java-driver-$module:$version"

    val cassandra_java_driver_core get() = cassandra("core")
    val cassandra_java_driver_core_shaded get() = cassandra("core-shaded")
    val cassandra_java_driver_mapper_processor get() = cassandra("mapper-processor")
    val cassandra_java_driver_mapper_runtime get() = cassandra("mapper-runtime")
    val cassandra_java_driver_metrics_micrometer get() = cassandra("metrics-micrometer")
    val cassandra_java_driver_metrics_microprofile get() = cassandra("metrics-microprofile")
    val cassandra_java_driver_query_builder get() = cassandra("query-builder")
    val cassandra_java_driver_test_infra get() = cassandra("test-infra")

    // ScyllaDB
    fun scyllaJava(module: String, version: String = Versions.scylla_java) = "com.scylladb:java-driver-$module:$version"
    val scylla_java_driver_core get() = scyllaJava("core")
    val scylla_java_driver_query_builder get() = scyllaJava("query-builder")
    val scylla_java_driver_mapper_processor get() = scyllaJava("mapper-processor")
    val scylla_java_driver_mapper_runtime get() = scyllaJava("mapper-runtime")
    val scylla_java_driver_metrics_micrometer get() = scyllaJava("metrics-micrometer")

    // ElasticSearch
    fun elasticsearch(module: String) = "org.elasticsearch.client:elasticsearch-$module:${Versions.elasticsearch}"
    val elasticsearch_rest_high_level_client get() = elasticsearch("rest-high-level-client")
    val elasticsearch_rest_client get() = elasticsearch("rest-client")
    val elasticsearch_rest_client_sniffer get() = elasticsearch("rest-client-sniffer")

    // InfluxDB
    val influxdb_java get() = "org.influxdb:influxdb-java:2.22"
    val influxdb_spring_data get() = "com.github.miwurster:spring-data-influxdb:1.8"

    // RabbitMQ
    val amqp_client get() = "com.rabbitmq:amqp-client:5.17.0"

    // Kafka
    fun kafka(module: String) = "org.apache.kafka:$module:${Versions.kafka}"
    val kafka_clients get() = kafka("kafka-clients")
    val kafka_generator get() = kafka("generator")
    val kafka_server_common get() = kafka("kafka-server-common")
    val kafka_streams get() = kafka("kafka-streams")
    val kafka_streams_test_utils get() = kafka("kafka-streams-test-utils")

    // Spring Kafka
    val spring_kafka get() = "org.springframework.kafka:spring-kafka:2.9.4"

    // Pulsar
    val pulsar_client get() = "org.apache.pulsar:pulsar-client:2.7.0"

    // Nats
    // https://github.com/nats-io/nats.java
    val jnats get() = "io.nats:jnats:2.16.10"

    // Zipkin
    val zipkin_brave get() = "io.zipkin.brave:brave:5.15.1"
    val zipkin_brave_tests get() = "io.zipkin.brave:brave-tests:5.15.1"

    // Hashicorp Vault
    val vault_java_driver get() = "com.bettercloud:vault-java-driver:5.1.0"

    // Hibernate
    fun hibernate(module: String) = "org.hibernate:hibernate-$module:${Versions.hibernate}"
    val hibernate_core get() = hibernate("core")
    val hibernate_jcache get() = hibernate("jcache")
    val hibernate_micrometer get() = hibernate("micrometer")
    val hibernate_testing get() = hibernate("testing")
    val hibernate_envers get() = hibernate("envers")
    val hibernate_jpamodelgen get() = hibernate("jpamodelgen")
    val hibernate_hikaricp get() = hibernate("hikaricp")
    val hibernate_spatial get() = hibernate("spatial")

    val hibernate_reactive_core get() = "org.hibernate.reactive:hibernate-reactive-core:${Versions.hibernate_reactive}"

    val javassist get() = "org.javassist:javassist:3.29.2-GA"

    // Validators
    val hibernate_validator get() = "org.hibernate:hibernate-validator:${Versions.hibernate_validator}"
    val hibernate_validator_annotation_processor get() = "org.hibernate:hibernate-validator-annotation-processor:${Versions.hibernate_validator}"

    // QueryDSL
    fun querydsl(module: String) = "com.querydsl:querydsl-$module:${Versions.querydsl}"
    val querydsl_apt get() = querydsl("apt")
    val querydsl_core get() = querydsl("core")
    val querydsl_jpa get() = querydsl("jpa")
    val querydsl_sql get() = querydsl("sql")
    val querydsl_kotlin get() = querydsl("kotlin")
    val querydsl_kotlin_codegen get() = querydsl("kotlin-codegen")

    // MyBais
    val mybatis get() = "org.mybatis:mybatis:3.5.13"
    val mybatis_spring get() = "org.mybatis:mybatis-spring:2.1.0"
    val mybatis_dynamic_sql get() = "org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.5.0"

    // blaze-persistence
    fun blazePersistence(module: String, version: String = Versions.blaze_persistence): String =
        "com.blazebit:blaze-persistence-$module:$version"

    val blaze_persistence_core_api get() = blazePersistence("core-api")
    val blaze_persistence_core_impl get() = blazePersistence("core-impl")
    val blaze_persistence_entity_view_processor get() = blazePersistence("entity-view-processor")
    val blaze_persistence_jpa_criteria_api get() = blazePersistence("jpa-criteria-api")
    val blaze_persistence_jpa_criteria_impl get() = blazePersistence("jpa-criteria-impl")

    fun blazePersistenceIntegration(module: String) = blazePersistence("integration-$module")

    val blaze_persistence_integration_quarkus get() = blazePersistenceIntegration("quarkus")
    val blaze_persistence_integration_hibernate_5 get() = blazePersistenceIntegration("hibernate-5")
    val blaze_persistence_integration_hibernate_5_6 get() = blazePersistenceIntegration("hibernate-5.6")
    val blaze_persistence_integration_jackson get() = blazePersistenceIntegration("jackson")
    val blaze_persistence_integration_jaxrs_jackson get() = blazePersistenceIntegration("jaxrs-jackson")
    val blaze_persistence_integration_entity_view_spring get() = blazePersistenceIntegration("entity-view-spring")

    // MyBatis Mapping 에 사용한다
    val byte_buddy = "net.bytebuddy:byte-buddy:1.14.4"

    val hikaricp get() = "com.zaxxer:HikariCP:5.0.1"
    val tomcat_jdbc get() = "org.apache.tomcat:tomcat-jdbc:9.0.36"

    val mysql_connector_j get() = "com.mysql:mysql-connector-j:8.0.32"
    val mariadb_java_client get() = "org.mariadb.jdbc:mariadb-java-client:3.1.3"
    val postgresql_driver get() = "org.postgresql:postgresql:42.6.0"
    val oracle_ojdbc8 get() = "com.oracle.ojdbc:ojdbc8:19.3.0.0"

    // NOTE: Apache Ignite 에서는 꼭 1.4.197 를 써야 합니다.
    val h2 get() = "com.h2database:h2:1.4.197"

    // MyBatis 테스트 시에 h2 v2 를 사용한다
    val h2_v2 get() = "com.h2database:h2:2.1.214"
    val hsqldb get() = "org.hsqldb:hsqldb:2.5.1"
    val flyway_core get() = "org.flywaydb:flyway-core:8.5.9"

    // UUID Generator
    val java_uuid_generator get() = "com.fasterxml.uuid:java-uuid-generator:4.1.0"
    val uuid_creator get() = "com.github.f4b6a3:uuid-creator:1.3.9"

    // Cache2K
    fun cache2k(module: String, version: String = Versions.cache2k) = "org.cache2k:cache2k-$module:$version"
    val cache2k_api get() = cache2k("api")
    val cache2k_core get() = cache2k("core")
    val cache2k_jcache get() = cache2k("jcache")
    val cache2k_micrometer get() = cache2k("micrometer")
    val cache2k_spring get() = cache2k("srping")

    // Caffeine
    fun caffeine(module: String, version: String = Versions.caffeine) = "com.github.ben-manes.caffeine:$module:$version"
    val caffeine get() = caffeine("caffeine")
    val caffeine_jcache get() = caffeine("jcache")

    val ehcache get() = "org.ehcache:ehcache:${Versions.ehcache}"
    val ehcache_clustered get() = "org.ehcache:ehcache-clustered:${Versions.ehcache}"
    val ehcache_transactions get() = "org.ehcache:ehcache-transactions:${Versions.ehcache}"

    // Apache Ignite
    fun ignite(module: String, version: String = Versions.ignite) = "org.apache.ignite:ignite-$module:$version"

    val ignite_aop get() = ignite("aop")
    val ignite_aws get() = ignite("aws")
    val ignite_cassandra_store get() = ignite("cassandra-store")
    val ignite_clients get() = ignite("client")
    val ignite_compress get() = ignite("compress")
    val ignite_core get() = ignite("core")
    val ignite_direct_io get() = ignite("direct-io")
    val ignite_hibernate_core get() = ignite("hibernate-core")
    val ignite_indexing get() = ignite("indexing")
    val ignite_jta get() = ignite("jta")
    val ignite_kafka get() = ignite("kafka")
    val ignite_kubenetes get() = ignite("kubenetes")
    val ignite_rest_http get() = ignite("rest-http")
    val ignite_slf4j get() = ignite("slf4j")
    val ignite_spring get() = ignite("spring")
    val ignite_tools get() = ignite("tools")
    val ignite_web get() = ignite("web")
    val ignite_zookeeper get() = ignite("zookeeper")

    // https://ignite.apache.org/docs/latest/extensions-and-integrations/spring/spring-data
    val ignite_spring_data_2_2_ext get() = ignite("spring-data-2.2-ext", "1.0.0")

    // https://ignite.apache.org/docs/latest/extensions-and-integrations/spring/spring-caching
    val ignite_spring_cache_ext get() = ignite("spring-cache-ext", "1.0.0")

    val hazelcast get() = "com.hazelcast:hazelcast:${Versions.hazelcast}"
    val hazelcast_spring get() = "com.hazelcast:hazelcast-spring:${Versions.hazelcast}"
    val hazelcast_client get() = "com.hazelcast:hazelcast-client:${Versions.hazelcast_client}"

    // CSV parsers
    val univocity_parsers get() = "com.univocity:univocity-parsers:2.9.1"

    val objenesis get() = "org.objenesis:objenesis:3.1"

    val ow2_asm get() = "org.ow2.asm:asm:${Versions.ow2_asm}"
    val ow2_asm_commons get() = "org.ow2.asm:asm-commons:${Versions.ow2_asm}"
    val ow2_asm_util get() = "org.ow2.asm:asm-util:${Versions.ow2_asm}"
    val ow2_asm_tree get() = "org.ow2.asm:asm-tree:${Versions.ow2_asm}"

    // junit 5.4+ 부터는 junit-jupiter 만 있으면 됩니다.
    val junit_bom get() = "org.junit:junit-bom:${Versions.junit_jupiter}"
    fun junitJupiter(module: String = "") =
        "org.junit.jupiter:junit-jupiter" + (if (module.isBlank()) "" else "-") + module

    val junit_jupiter get() = junitJupiter()
    val junit_jupiter_api get() = junitJupiter("api")
    val junit_jupiter_engine get() = junitJupiter("engine")
    val junit_jupiter_migrationsupport get() = junitJupiter("migrationsupport")
    val junit_jupiter_params get() = junitJupiter("params")

    fun junitPlatform(module: String) = "org.junit.platform:junit-platform-$module"

    val junit_platform_commons get() = junitPlatform("commons")
    val junit_platform_engine get() = junitPlatform("engine")
    val junit_platform_launcher get() = junitPlatform("launcher")
    val junit_platform_runner get() = junitPlatform("runner")
    val junit_platform_suite_api get() = junitPlatform("suite-api")
    val junit_platform_suite_engine get() = junitPlatform("suite-engine")

    val junit_vintage_engine get() = "org.junit.vintage:junit-vintage-engine:${Versions.junit_jupiter}"

    val kluent get() = "org.amshove.kluent:kluent:${Versions.kluent}"
    val assertj_core get() = "org.assertj:assertj-core:${Versions.assertj_core}"

    val mockk get() = "io.mockk:mockk:${Versions.mockk}"
    val springmockk get() = "com.ninja-squad:springmockk:${Versions.springmockk}"

    val mockito_core get() = "org.mockito:mockito-core:${Versions.mockito}"
    val mockito_junit_jupiter get() = "org.mockito:mockito-junit-jupiter:${Versions.mockito}"
    val mockito_kotlin get() = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    val jmock_junit5 get() = "org.jmock:jmock-junit5:2.12.0"

    // Awaitility (https://github.com/awaitility/awaitility)
    val awaitility get() = "org.awaitility:awaitility:${Versions.awaitility}"
    val awaitility_kotlin get() = "org.awaitility:awaitility-kotlin:${Versions.awaitility}"

    val datafaker get() = "net.datafaker:datafaker:${Versions.datafaker}"
    val snakeyaml get() = "org.yaml:snakeyaml:${Versions.snakeyaml}"
    val random_beans get() = "io.github.benas:random-beans:${Versions.random_beans}"

    val mockserver_netty get() = "org.mock-server:mockserver-netty:5.10.0"
    val mockserver_client_java get() = "org.mock-server:mockserver-client-java:5.10.0"

    val system_rules get() = "com.github.stefanbirkner:system-rules:1.19.0"

    val jmh_core get() = "org.openjdk.jmh:jmh-core:${Versions.jmh}"
    val jmh_generator_annprocess get() = "org.openjdk.jmh:jmh-generator-annprocess:${Versions.jmh}"

    // Testcontainers
    fun testcontainers(module: String) = "org.testcontainers:$module:${Versions.testcontainers}"

    val testcontainers_bom get() = testcontainers("testcontainers-bom")
    val testcontainers get() = testcontainers("testcontainers")
    val testcontainers_junit_jupiter get() = testcontainers("junit-jupiter")
    val testcontainers_cassandra get() = testcontainers("cassandra")
    val testcontainers_cockroachdb get() = testcontainers("cockroachdb")
    val testcontainers_couchbase get() = testcontainers("couchbase")
    val testcontainers_elasticsearch get() = testcontainers("elasticsearch")
    val testcontainers_influxdb get() = testcontainers("influxdb")
    val testcontainers_dynalite get() = testcontainers("dynalite")
    val testcontainers_mariadb get() = testcontainers("mariadb")
    val testcontainers_mongodb get() = testcontainers("mongodb")
    val testcontainers_mysql get() = testcontainers("mysql")
    val testcontainers_postgresql get() = testcontainers("postgresql")
    val testcontainers_oracle_xe get() = testcontainers("oracle-xe")
    val testcontainers_kafka get() = testcontainers("kafka")
    val testcontainers_pulsar get() = testcontainers("pulsar")
    val testcontainers_rabbitmq get() = testcontainers("rabbitmq")
    val testcontainers_vault get() = testcontainers("vault")

    // the Atlassian's LocalStack, 'a fully functional local AWS cloud stack'.
    val testcontainers_localstack get() = testcontainers("localstack")
    val testcontainers_mockserver get() = testcontainers("mockserver")

    val testcontainers_nginx get() = testcontainers("nginx")
    val testcontainers_r2dbc get() = testcontainers("r2dbc")

    val testcontainers_gcloud get() = testcontainers("gcloud")

    // kubernetes
    val testcontainers_k3s get() = testcontainers("k3s")

    val fabric8_kubernetes_client_bom get() = "io.fabric8:kubernetes-client-bom:5.12.2"
    val fabric8_kubernetes_client get() = "io.fabric8:kubernetes-client:5.12.2"
    val kubernetes_client_java get() = "io.kubernetes:client-java:15.0.1"

    // Apple Silicon에서 testcontainers 를 사용하기 위해 참조해야 합니다.
    val jna get() = "net.java.dev.jna:jna:${Versions.jna}"
    val jna_platform get() = "net.java.dev.jna:jna-platform:${Versions.jna}"

    // wiremock
    val wiremock_jre8 get() = "com.github.tomakehurst:wiremock-jre8:2.33.2"

    // Springdoc OpenAPI
    val springdoc_openapi_ui get() = "org.springdoc:springdoc-openapi-ui:${Versions.springdoc_openapi}"
    val springdoc_openapi_webflux_ui get() = "org.springdoc:springdoc-openapi-webflux-ui:${Versions.springdoc_openapi}"
    val springdoc_openapi_security get() = "org.springdoc:springdoc-openapi-security:${Versions.springdoc_openapi}"

    // Swagger
    val swagger_annotations get() = "io.swagger:swagger-annotations:${Versions.swagger}"
    val springfox_boot_starter get() = "io.springfox:springfox-boot-starter:${Versions.springfox_swagger}"

    // Problem for Spring
    val problem_spring_web get() = "org.zalando:problem-spring-web:${Versions.problem}"
    val problem_spring_webflux get() = "org.zalando:problem-spring-webflux:${Versions.problem}"

    // https://mvnrepository.com/artifact/com.github.maricn/logback-slack-appender
    val logback_slack_appender get() = "com.github.maricn:logback-slack-appender:${Versions.logback_slack_appender}"
    val sentry_logback get() = "io.sentry:sentry-logback:${Versions.sentry_logback}"

    // ArchUnit - https://www.archunit.org/userguide/html/000_Index.html
    val archunit get() = "com.tngtech.archunit:archunit:${Versions.archunit}"
    val archunit_junit5 get() = "com.tngtech.archunit:archunit-junit5:${Versions.archunit}"

    // Detekt Plugins
    val detekt_formatting get() = "io.gitlab.arturbosch.detekt:detekt-formatting:${Plugins.Versions.detekt}"

    // WebJars
    fun webjar(module: String, version: String) = "org.webjars:$module:$version"
}
