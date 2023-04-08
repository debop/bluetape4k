object BuildPlugins {

    object Versions {
        const val detekt = "1.14.2"
        const val dokka = "0.10.1"
        const val dependency_management = "1.0.10.RELEASE"
        const val propdeps = "0.0.10"
        const val jooq = "3.0.3"
        const val protobuf = "0.8.13"
        const val avro = "0.21.0"
        const val jarTest = "1.0.1"
        const val testLogger = "2.1.1"
        const val kotlinx_benchmark = "0.3.1"
        const val spring_boot = "2.5.3"
    }

    const val detekt = "io.gitlab.arturbosch.detekt"
    const val dokka = "org.jetbrains.dokka"
    const val dependency_management = "io.spring.dependency-management"
    const val spring_boot = "org.springframework.boot"

    const val propdeps = "cn.bestwu.propdeps"
    const val propdeps_idea = "cn.bestwu.propdeps-idea"
    const val propdeps_maven = "cn.bestwu.propdeps-maven"

    const val jooq = "nu.studer.jooq"

    // https://github.com/google/protobuf-gradle-plugin
    const val protobuf = "com.google.protobuf"

    // https://github.com/davidmc24/gradle-avro-plugin
    const val avro = "com.commercehub.gradle.plugin.avro"

    const val jarTest = "com.github.hauner.jarTest"
    const val testLogger = "com.adarshr.test-logger"
    const val kotlinx_benchmark = "org.jetbrains.kotlinx.benchmark"
}

object Versions {

    const val kotlin = "1.8.20"
    const val kotlinx_coroutines = "1.6.4"
    const val atomicfu = "0.14.4"
    const val kotlinx_io = "0.1.16"
    const val kotlinx_benchmark = BuildPlugins.Versions.kotlinx_benchmark
    const val ktor = "1.4.1"

    const val spring_boot = BuildPlugins.Versions.spring_boot
    const val spring_cloud = "Hoxton.SR9"
    const val resilience4j = "1.7.0"
    const val netty = "4.1.53.Final"

    const val aws = "1.11.891"
    const val aws2 = "2.13.59"

    const val grpc = "1.38.0"
    const val grpc_kotlin = "1.1.0"
    const val protobuf = "3.17.1"
    const val avro = "1.10.0"   // 1.9.+ 은 jackson-dataformat-avro 에서 아직 지원하지 않습니다.

    const val asynchttpclient = "2.12.1"
    const val retrofit2 = "2.9.0"
    const val okhttp3 = "4.9.1"
    const val jasync_sql = "1.1.7"
    const val mapstruct = "1.4.2.Final"
    const val jackson = "2.12.3"

    const val reflectasm = "1.11.9"
    const val mongo_driver = "3.12.3"
    const val lettuce = "6.1.2.RELEASE"
    const val redisson = "3.15.5"

    const val hibernate = "5.4.32.Final"
    const val hibernate_validator = "7.0.1.Final"
    const val querydsl = "4.4.0"

    const val slf4j = "1.7.30"
    const val logback = "1.2.3"
    const val log4j = "2.13.3"

    const val metrics = "4.1.13"
    const val prometheus = "0.9.0"
    const val micrometer = "1.7.0"

    const val cache2k = "1.6.0.Final"
    const val caffeine = "2.8.5"

    const val ignite = "2.8.1"
    const val hazelcast = "3.12.9"
    const val cassandra = "4.9.0"
    const val scylla = "3.7.1-scylla-2"
    const val elasticsearch = "7.9.1"

    const val eclipse_collections = "10.4.0"
    const val koin = "2.1.6"

    const val junit_jupiter = "5.7.2"
    const val junit_platform = "1.7.2"
    const val assertj_core = "3.19.0"
    const val mockk = "1.11.0"
    const val mockito = "3.5.13"
    const val jmh = "1.26"
    const val testcontainers = "1.15.3"
}

object Libs {

    const val jetbrains_annotations = "org.jetbrains:annotations:20.1.0"

    const val kotlin_bom = "org.jetbrains.kotlin:kotlin-bom:${Versions.kotlin}"
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib"
    const val kotlin_stdlib_common = "org.jetbrains.kotlin:kotlin-stdlib-common"
    const val kotlin_stdlib_jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7"
    const val kotlin_stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect"
    const val kotlin_test = "org.jetbrains.kotlin:kotlin-test"
    const val kotlin_test_common = "org.jetbrains.kotlin:kotlin-test-common"
    const val kotlin_test_junit5 = "org.jetbrains.kotlin:kotlin-test-junit5"

    // Kotlin 1.3.40 부터는 kotlin-scripting-jsr223 만 참조하면 됩니다.
    const val kotlin_scripting_jsr223 = "org.jetbrains.kotlin:kotlin-scripting-jsr223"
    const val kotlin_compiler = "org.jetbrains.kotlin:kotlin-compiler"

    // Kotlin 1.4+ 부터는 kotlin-scripting-dependencies 를 참조해야 합니다.
    const val kotlin_scripting_dependencies = "org.jetbrains.kotlin:kotlin-scripting-dependencies"

    const val kotlin_compiler_embeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable"
    const val kotlin_daemon_client = "org.jetbrains.kotlin:kotlin-daemon-client"
    const val kotlin_scripting_common = "org.jetbrains.kotlin:kotlin-scripting-common"
    const val kotlin_scripting_compiler_embeddable = "org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable"
    const val kotlin_scripting_jvm = "org.jetbrains.kotlin:kotlin-scripting-jvm"
    const val kotlin_script_runtime = "org.jetbrains.kotlin:kotlin-script-runtime"
    const val kotlin_script_util = "org.jetbrains.kotlin:kotlin-script-util"

    const val kotlinx_coroutines_bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:${Versions.kotlinx_coroutines}"
    const val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core"
    const val kotlinx_coroutines_core_common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common"
    const val kotlinx_coroutines_debug = "org.jetbrains.kotlinx:kotlinx-coroutines-debug"
    const val kotlinx_coroutines_jdk7 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk7"
    const val kotlinx_coroutines_jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8"
    const val kotlinx_coroutines_jdk9 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk9"
    const val kotlinx_coroutines_reactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive"
    const val kotlinx_coroutines_reactor = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor"
    const val kotlinx_coroutines_rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2"
    const val kotlinx_coroutines_rx3 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx3"
    const val kotlinx_coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test"

    const val atomicfu = "org.jetbrains.kotlinx:atomicfu:${Versions.atomicfu}"

    const val kotlinx_io = "org.jetbrains.kotlinx:kotlinx-io:${Versions.kotlinx_io}"
    const val kotlinx_io_jvm = "org.jetbrains.kotlinx:kotlinx-io-jvm:${Versions.kotlinx_io}"
    const val kotlinx_coroutines_io_jvm = "org.jetbrains.kotlinx:kotlinx-coroutines-io-jvm:${Versions.kotlinx_io}"

    const val kotlinx_benchmark_runtime =
        "org.jetbrains.kotlinx:kotlinx.benchmark.runtime:${Versions.kotlinx_benchmark}"
    const val kotlinx_benchmark_runtime_jvm =
        "org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:${Versions.kotlinx_benchmark}"

    // Ktor
    const val ktor_client_apache = "io.ktor:ktor-client-apache:${Versions.ktor}"
    const val ktor_client_cio = "io.ktor:ktor-client-cio:${Versions.ktor}"

    // javax api (Deperecated) (Use jakarta library)
    const val javax_activation_api = "javax.activation:javax.activation-api:1.2.0"
    const val javax_annotation_api = "javax.annotation:javax.annotation-api:1.3.2"
    const val javax_cache_api = "javax.cache:cache-api:1.1.1"
    const val javax_inject = "javax.inject:javax.inject:1"
    const val javax_servlet_api = "javax.servlet:javax.servlet-api:4.0.1"
    const val javax_transaction_api = "javax.transaction:jta:1.1"
    const val javax_validation_api = "javax.validation:validation-api:2.0.1.Final"

    // jakarta
    const val jakarta_activation_api = "jakarta.activation:jakarta.activation-api:1.2.2"
    const val jakarta_annotation_api = "jakarta.annotation:jakarta.annotation-api:1.3.5"
    const val jakarta_el_api = "jakarta.el:jakarta.el-api:3.0.3"
    const val jakarta_inject_api = "jakarta.inject:jakarta.inject-api:1.0.1"
    const val jakarta_interceptor_api = "jakarta.interceptor:jakarta.interceptor-api:1.2.5"
    const val jakarta_jms_api = "jakarta.jms:jakarta.jms-api:2.0.3"
    const val jakarta_json_api = "jakarta.json:jakarta.json-api:1.1.6"
    const val jakarta_persistence_api = "jakarta.persistence:jakarta.persistence-api:2.2.3"
    const val jakarta_servlet_api = "jakarta.servlet:jakarta.servlet-api:4.0.4"
    const val jakarta_transaction_api = "jakarta.transaction:jakarta.transaction-api:1.3.3"
    const val jakarta_validation_api = "jakarta.validation:jakarta.validation-api:2.0.2"

    // Java Money
    const val javax_money_api = "javax.money:money-api:1.1"
    const val javamoney_moneta = "org.javamoney:moneta:1.4"

    // Apache Commons
    const val commons_beanutils = "commons-beanutils:commons-beanutils:1.9.4"
    const val commons_compress = "org.apache.commons:commons-compress:1.20"
    const val commons_codec = "commons-codec:commons-codec:1.15"
    const val commons_collections4 = "org.apache.commons:commons-collections4:4.4"
    const val commons_csv = "org.apache.commons:commons-csv:1.8"
    const val commons_digest3 = "org.apache.commons:commons-digester3:3.2"
    const val commons_exec = "org.apache.commons:commons-exec:1.3"
    const val commons_io = "commons-io:commons-io:2.8.0"
    const val commons_lang3 = "org.apache.commons:commons-lang3:3.11"
    const val commons_logging = "commons-logging:commons-logging:1.2"
    const val commons_math3 = "org.apache.commons:commons-math3:3.6.1"
    const val commons_pool2 = "org.apache.commons:commons-pool2:2.8.0"
    const val commons_rng_simple = "org.apache.commons:commons-rng-simple:1.3"
    const val commons_text = "org.apache.commons:commons-text:1.9"
    const val commons_validator = "commons-validator:commons-validator:1.6"

    const val colt = "colt:colt:1.2.0"

    // typesafe config
    const val typesafe_config = "com.typesafe:config:1.4.0"

    const val slf4j_api = "org.slf4j:slf4j-api:${Versions.slf4j}"
    const val slf4j_simple = "org.slf4j:slf4j-simple:${Versions.slf4j}"
    const val slf4j_log4j12 = "org.slf4j:slf4j-log4j12:${Versions.slf4j}"
    const val jcl_over_slf4j = "org.slf4j:jcl-over-slf4j:${Versions.slf4j}"
    const val jul_to_slf4j = "org.slf4j:jul-to-slf4j:${Versions.slf4j}"
    const val log4j_over_slf4j = "org.slf4j:log4j-over-slf4j:${Versions.slf4j}"

    const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"

    const val log4j_bom = "org.apache.logging.log4j:log4j-bom:${Versions.log4j}"
    const val log4j_api = "org.apache.logging.log4j:log4j-api"
    const val log4j_core = "org.apache.logging.log4j:log4j-core"
    const val log4j_jcl = "org.apache.logging.log4j:log4j-jcl"
    const val log4j_jul = "org.apache.logging.log4j:log4j-jul"
    const val log4j_slf4j_impl = "org.apache.logging.log4j:log4j-slf4j-impl"
    const val log4j_web = "org.apache.logging.log4j:log4j-web"

    const val findbugs = "com.google.code.findbugs:jsr305:3.0.2"
    const val guava = "com.google.guava:guava:28.0-jre"
    const val joda_time = "joda-time:joda-time:2.10.6"
    const val joda_convert = "org.joda:joda-convert:2.2.1"

    const val eclipse_collections = "org.eclipse.collections:eclipse-collections:${Versions.eclipse_collections}"
    const val eclipse_collections_forkjoin =
        "org.eclipse.collections:eclipse-collections-forkjoin:${Versions.eclipse_collections}"
    const val eclipse_collections_testutils =
        "org.eclipse.collections:eclipse-collections-testutils:${Versions.eclipse_collections}"

    const val fst = "de.ruedigermoeller:fst:2.57"
    const val kryo = "com.esotericsoftware:kryo:4.0.2"
    const val kryo_serializers = "de.javakaffee:kryo-serializers:0.45"

    // Spring Boot
    const val spring_boot_dependencies = "org.springframework.boot:spring-boot-dependencies:${Versions.spring_boot}"

    // Spring Cloud
    const val spring_cloud_dependencies = "org.springframework.cloud:spring-cloud-dependencies:${Versions.spring_cloud}"

    // Resilience4j
    const val resilience4j_bom = "io.github.resilience4j:resilience4j-bom:${Versions.resilience4j}"
    const val resilience4j_all = "io.github.resilience4j:resilience4j-all"
    const val resilience4j_annotations = "io.github.resilience4j:resilience4j-annotations"
    const val resilience4j_bulkhead = "io.github.resilience4j:resilience4j-bulkhead"
    const val resilience4j_cache = "io.github.resilience4j:resilience4j-cache"
    const val resilience4j_circuitbreaker = "io.github.resilience4j:resilience4j-circuitbreaker"
    const val resilience4j_circularbuffer = "io.github.resilience4j:resilience4j-circularbuffer"
    const val resilience4j_consumer = "io.github.resilience4j:resilience4j-consumer"
    const val resilience4j_core = "io.github.resilience4j:resilience4j-core"
    const val resilience4j_feign = "io.github.resilience4j:resilience4j-feign"
    const val resilience4j_framework_common = "io.github.resilience4j:resilience4j-framework-common"
    const val resilience4j_kotlin = "io.github.resilience4j:resilience4j-kotlin"
    const val resilience4j_metrics = "io.github.resilience4j:resilience4j-metrics"
    const val resilience4j_micrometer = "io.github.resilience4j:resilience4j-micrometer"
    const val resilience4j_prometheus = "io.github.resilience4j:resilience4j-prometheus"
    const val resilience4j_ratelimiter = "io.github.resilience4j:resilience4j-ratelimiter"
    const val resilience4j_ratpack = "io.github.resilience4j:resilience4j-ratpack"
    const val resilience4j_reactor = "io.github.resilience4j:resilience4j-reactor"
    const val resilience4j_retrofit = "io.github.resilience4j:resilience4j-retrofit"
    const val resilience4j_retry = "io.github.resilience4j:resilience4j-retry"
    const val resilience4j_rxjava2 = "io.github.resilience4j:resilience4j-rxjava2"
    const val resilience4j_rxjava3 = "io.github.resilience4j:resilience4j-rxjava3"
    const val resilience4j_spring = "io.github.resilience4j:resilience4j-spring"
    const val resilience4j_spring_boot2 = "io.github.resilience4j:resilience4j-spring-boot2"
    const val resilience4j_spring_cloud2 = "io.github.resilience4j:resilience4j-spring-cloud2"
    const val resilience4j_timelimiter = "io.github.resilience4j:resilience4j-timelimiter"
    const val resilience4j_vertx = "io.github.resilience4j:resilience4j-vertx"

    // Netty
    const val netty_bom = "io.netty:netty-bom:${Versions.netty}"
    const val netty_all = "io.netty:netty-all"
    const val netty_common = "io.netty:netty-common"
    const val netty_buffer = "io.netty:netty-buffer"
    const val netty_codec = "io.netty:netty-codec"
    const val netty_codec_dns = "io.netty:netty-codec-dns"
    const val netty_codec_http = "io.netty:netty-codec-http"
    const val netty_codec_http2 = "io.netty:netty-codec-http2"
    const val netty_codec_socks = "io.netty:netty-codec-socks"
    const val netty_handler = "io.netty:netty-handler"
    const val netty_handler_proxy = "io.netty:netty-handler-proxy"
    const val netty_resolver = "io.netty:netty-resolver"
    const val netty_resolver_dns = "io.netty:netty-resolver-dns"
    const val netty_transport = "io.netty:netty-transport"
    const val netty_transport_native_epoll = "io.netty:netty-transport-native-epoll:${Versions.netty}"
    const val netty_transport_native_kqueue = "io.netty:netty-transport-native-kqueue:${Versions.netty}"

    // gRPC
    const val grpc_bom = "io.grpc:grpc-bom:${Versions.grpc}"
    const val grpc_alts = "io.grpc:grpc-alts"
    const val grpc_api = "io.grpc:grpc-api"
    const val grpc_auth = "io.grpc:grpc-auth"
    const val grpc_context = "io.grpc:grpc-context"
    const val grpc_core = "io.grpc:grpc-core"
    const val grpc_grpclb = "io.grpc:grpc-grpclb"
    const val grpc_protobuf = "io.grpc:grpc-protobuf"
    const val grpc_protobuf_lite = "io.grpc:grpc-protobuf-lite"
    const val grpc_stub = "io.grpc:grpc-stub"
    const val grpc_services = "io.grpc:grpc-services"
    const val grpc_netty = "io.grpc:grpc-netty"
    const val grpc_netty_shaded = "io.grpc:grpc-netty-shaded"
    const val grpc_okhttp = "io.grpc:grpc-okhttp"
    const val grpc_protoc_gen_grpc_java = "io.grpc:protoc-gen-grpc-java"
    const val grpc_testing = "io.grpc:grpc-testing"

    // gRPC Kotlin
    const val grpc_kotlin_stub = "io.grpc:grpc-kotlin-stub:${Versions.grpc_kotlin}"
    const val grpc_protoc_gen_grpc_kotlin = "io.grpc:protoc-gen-grpc-kotlin:${Versions.grpc_kotlin}"

    const val protobuf_bom = "com.google.protobuf:protobuf-bom:${Versions.protobuf}"
    const val protobuf_protoc = "com.google.protobuf:protoc:${Versions.protobuf}"
    const val protobuf_java = "com.google.protobuf:protobuf-java"
    const val protobuf_java_util = "com.google.protobuf:protobuf-java-util"

    const val avro = "org.apache.avro:avro:${Versions.avro}"
    const val avro_ipc = "org.apache.avro:avro-ipc:${Versions.avro}"
    const val avro_ipc_netty = "org.apache.avro:avro-ipc-netty:${Versions.avro}"
    const val avro_compiler = "org.apache.avro:avro-compiler:${Versions.avro}"
    const val avro_protobuf = "org.apache.avro:avro-protobuf:${Versions.avro}"


    // Amazon
    private const val aws_group = "com.amazonaws"
    const val aws_bom = "$aws_group:aws-java-sdk-bom:${Versions.aws}"
    const val aws_java_sdk = "$aws_group:aws-java-sdk"
    const val aws_java_sdk_s3 = "$aws_group:aws-java-sdk-s3"
    const val aws_java_sdk_dynamodb = "$aws_group:aws-java-sdk-dynamodb"
    const val aws_java_sdk_sns = "$aws_group:aws-java-sdk-sns"
    const val aws_java_sdk_sqs = "$aws_group:aws-java-sdk-sqs"
    const val aws_java_sdk_sts = "$aws_group:aws-java-sdk-sts"
    const val aws_java_sdk_ec2 = "$aws_group:aws-java-sdk-ec2"
    const val aws_java_sdk_test_utils = "$aws_group:aws-java-sdk-test-utils:${Versions.aws}"

    // Amazon AWS V2
    private const val aws2_group = "software.amazon.awssdk"

    // const val aws2_bom = "$aws_group:bom:${Versions.aws2}"
    const val aws2_elasticache = "$aws2_group:elasticache:${Versions.aws2}"
    const val aws2_kafka = "$aws2_group:kafka:${Versions.aws2}"
    const val aws2_s3 = "$aws2_group:s3:${Versions.aws2}"


    // AsyncHttpClient
    const val async_http_client = "org.asynchttpclient:async-http-client:${Versions.asynchttpclient}"
    const val async_http_client_extras_retrofit2 =
        "org.asynchttpclient:async-http-client-extras-retrofit2:${Versions.asynchttpclient}"
    const val async_http_client_extras_rxjava2 =
        "org.asynchttpclient:async-http-client-extras-rxjava2:${Versions.asynchttpclient}"


    // Retrofit2
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit2}"
    const val retrofit2_adapter_java8 = "com.squareup.retrofit2:adapter-java8:${Versions.retrofit2}"
    const val retrofit2_adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit2}"
    const val retrofit2_adapter_rxjava3 = "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit2}"
    const val retrofit2_converter_jackson = "com.squareup.retrofit2:converter-jackson:${Versions.retrofit2}"
    const val retrofit2_converter_moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit2}"
    const val retrofit2_converter_protobuf = "com.squareup.retrofit2:converter-protobuf:${Versions.retrofit2}"
    const val retrofit2_converter_scalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit2}"
    const val retrofit2_mock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit2}"

    // https://github.com/JakeWharton/retrofit2-reactor-adapter/
    const val retrofit2_adapter_reactor = "com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0"

    // OkHttp3
    const val okhttp3_bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okhttp3}"
    const val okhttp3 = "com.squareup.okhttp3:okhttp"
    const val okhttp3_logging_interceptor = "com.squareup.okhttp3:logging-interceptor"
    const val okhttp3_mockwebserver = "com.squareup.okhttp3:mockwebserver"
    const val okhttp3_sse = "com.squareup.okhttp3:okhttp-sse"
    const val okhttp3_urlconnection = "com.squareup.okhttp3:okhttp-urlconnection"
    const val okhttp3_ws = "com.squareup.okhttp3:okhttp-ws"

    // ThreeTenBP
    const val threeten_bp = "org.threeten:threetenbp:1.4.0"

    const val zero_allocation_hashing = "net.openhft:zero-allocation-hashing:0.9"

    // google auto service
    const val google_auto_service = "com.google.auto.service:auto-service:1.0-rc6"

    // MapStruct
    const val mapstruct = "org.mapstruct:mapstruct:${Versions.mapstruct}"
    const val mapstruct_processor = "org.mapstruct:mapstruct-processor:${Versions.mapstruct}"

    // Jackson
    const val jackson_bom = "com.fasterxml.jackson:jackson-bom:${Versions.jackson}"
    const val jackson_annotations = "com.fasterxml.jackson.core:jackson-annotations"
    const val jackson_core = "com.fasterxml.jackson.core:jackson-core"
    const val jackson_databind = "com.fasterxml.jackson.core:jackson-databind"

    const val jackson_datatype_jsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
    const val jackson_datatype_jsr353 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr353"
    const val jackson_datatype_jdk8 = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8"
    const val jackson_datatype_joda = "com.fasterxml.jackson.datatype:jackson-datatype-joda"
    const val jackson_datatype_guava = "com.fasterxml.jackson.datatype:jackson-datatype-guava"

    const val jackson_dataformat_avro = "com.fasterxml.jackson.dataformat:jackson-dataformat-avro"
    const val jackson_dataformat_protobuf = "com.fasterxml.jackson.dataformat:jackson-dataformat-protobuf"
    const val jackson_dataformat_csv = "com.fasterxml.jackson.dataformat:jackson-dataformat-csv"
    const val jackson_dataformat_properties = "com.fasterxml.jackson.dataformat:jackson-dataformat-properties"
    const val jackson_dataformat_yaml = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml"

    const val jackson_module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin"
    const val jackson_module_paranamer = "com.fasterxml.jackson.module:jackson-module-paranamer"
    const val jackson_module_parameter_names = "com.fasterxml.jackson.module:jackson-module-parameter-names"
    const val jackson_module_afterburner = "com.fasterxml.jackson.module:jackson-module-afterburner"

    // jakarta.json-api 의 구현체
    const val javax_json = "org.glassfish:javax.json:1.1.4"

    const val gson = "com.google.code.gson:gson:2.8.6"
    const val gson_jodatime_serialisers =
        "com.fatboyindustrial.gson-jodatime-serialisers:gson-jodatime-serialisers:1.7.1"

    const val msgpack_core = "org.msgpack:msgpack-core:0.8.18"
    const val msgpack_jackson = "org.msgpack:jackson-dataformat-msgpack:0.8.18"

    const val protostuff_core = "io.protostuff:protostuff-core:1.6.0"
    const val protostuff_runtime = "io.protostuff:protostuff-runtime:1.6.0"
    const val protostuff_collectionschema = "io.protostuff:protostuff-collectionschema:1.6.0"

    // jasync-sql
    const val jasync_common = "com.github.jasync-sql:jasync-common:${Versions.jasync_sql}"
    const val jasync_mysql = "com.github.jasync-sql:jasync-mysql:${Versions.jasync_sql}"
    const val jasync_r2dbc_mysql = "com.github.jasync-sql:jasync-r2dbc-mysql:${Versions.jasync_sql}"
    const val jasync_postgresql = "com.github.jasync-sql:jasync-postgresql:${Versions.jasync_sql}"

    // Compression
    const val snappy_java = "org.xerial.snappy:snappy-java:1.1.8.4"
    const val lz4_java = "org.lz4:lz4-java:1.7.1"
    const val zstd_jni = "com.github.luben:zstd-jni:1.4.9-4"
    const val xz = "org.tukaani:xz:1.9"

    // Cryptography
    const val jasypt = "org.jasypt:jasypt:1.9.3"
    const val bouncycastle_bcprov = "org.bouncycastle:bcprov-jdk15on:1.66"
    const val bouncycastle_bcpkix = "org.bouncycastle:bcpkix-jdk15on:1.66"

    // MVEL
    const val mvel2 = "org.mvel:mvel2:2.4.10.Final"

    // Reactor
    const val reactor_core = "io.projectreactor:reactor-core"
    const val reactor_test = "io.projectreactor:reactor-test"
    const val reactor_netty = "io.projectreactor.netty:reactor-netty"

    // RxJava
    const val rxjava = "io.reactivex:rxjava:1.3.8"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.19"
    const val rxkotlin2 = "io.reactivex.rxjava2:rxkotlin:2.4.0"

    const val rxjava3 = "io.reactivex.rxjava3:rxjava:3.0.6"
    const val rxkotlin3 = "io.reactivex.rxjava3:rxkotlin:3.0.0"

    // Metrics
    const val metrics_bom = "io.dropwizard.metrics:metrics-bom:${Versions.metrics}"
    const val metrics_annotation = "io.dropwizard.metrics:metrics-annotation"
    const val metrics_core = "io.dropwizard.metrics:metrics-core"
    const val metrics_json = "io.dropwizard.metrics:metrics-json"
    const val metrics_jvm = "io.dropwizard.metrics:metrics-jvm"
    const val metrics_graphite = "io.dropwizard.metrics:metrics-graphite"
    const val metrics_healthchecks = "io.dropwizard.metrics:metrics-healthchecks"
    const val metrics_jcache = "io.dropwizard.metrics:metrics-jcache"
    const val metrics_jmx = "io.dropwizard.metrics:metrics-jmx"

    // Prometheus
    const val prometheus_simpleclient = "io.prometheus:simpleclient:${Versions.prometheus}"
    const val prometheus_simpleclient_common = "io.prometheus:simpleclient_common:${Versions.prometheus}"
    const val prometheus_simpleclient_dropwizard = "io.prometheus:simpleclient_dropwizard:${Versions.prometheus}"
    const val prometheus_simpleclient_httpserver = "io.prometheus:simpleclient_httpserver:${Versions.prometheus}"
    const val prometheus_simpleclient_pushgateway = "io.prometheus:simpleclient_pushgateway:${Versions.prometheus}"
    const val prometheus_simpleclient_spring_boot = "io.prometheus:simpleclient_spring_boot:${Versions.prometheus}"

    // Micrometer
    const val micrometer_bom = "io.micrometer:micrometer-bom:${Versions.micrometer}"
    const val micrometer_core = "io.micrometer:micrometer-core"
    const val micrometer_test = "io.micrometer:micrometer-test"
    const val micrometer_registry_prometheus = "io.micrometer:micrometer-registry-prometheus"
    const val micrometer_registry_graphite = "io.micrometer:micrometer-registry-graphite"
    const val micrometer_registry_jmx = "io.micrometer:micrometer-registry-jmx"

    const val latencyUtils = "org.latencyutils:LatencyUtils:2.0.3"
    const val hdrHistogram = "org.hdrhistogram:HdrHistogram:2.1.11"

    const val reflectasm: String = "com.esotericsoftware:reflectasm:${Versions.reflectasm}"

    // MongoDB
    const val mongo_java_driver = "org.mongodb:mongo-java-driver:${Versions.mongo_driver}"
    const val mongo_bson = "org.mongodb:bson:${Versions.mongo_driver}"
    const val mongo_driver = "org.mongodb:mongodb-driver:${Versions.mongo_driver}"
    const val mongo_driver_async = "org.mongodb:mongodb-driver-async:${Versions.mongo_driver}"
    const val mongo_driver_core = "org.mongodb:mongodb-driver-core:${Versions.mongo_driver}"
    const val mongo_driver_reactivestreams = "org.mongodb:mongodb-driver-reactivestreams:1.11.0"

    // ArangoDB
    const val arangodb_java_driver = "com.arangodb:arangodb-java-driver:6.5.0"
    const val arangodb_java_driver_async = "com.arangodb:arangodb-java-driver:6.0.0"
    const val arangodb_spring_data = "com.arangodb:arangodb-spring-data:3.2.3"

    // Redis
    const val lettuce_core = "io.lettuce:lettuce-core:${Versions.lettuce}"
    const val redisson = "org.redisson:redisson:${Versions.redisson}"
    const val redisson_spring_boot_starter = "org.redisson:redisson-spring-boot-starter:${Versions.redisson}"
    const val redisson_spring_data_21 = "org.redisson:redisson-spring-data-21:${Versions.redisson}"
    const val redisson_spring_data_22 = "org.redisson:redisson-spring-data-22:${Versions.redisson}"

    // Memcached
    const val folsom = "com.spotify:folsom:1.6.1"
    const val spymemcached = "net.spy:spymemcached:2.12.3"

    // Cassandra
    const val cassandra_java_core = "com.datastax.oss:java-driver-core:${Versions.cassandra}"
    const val cassandra_java_query_builder = "com.datastax.oss:java-driver-query-builder:${Versions.cassandra}"
    const val cassandra_java_mapper_runtime = "com.datastax.oss:java-driver-mapper-runtime:${Versions.cassandra}"

    // ScyllaDB
    const val scylla_driver_core = "com.scylladb:scylla-driver-core:${Versions.scylla}"
    const val scylla_driver_mapping = "com.scylladb:scylla-driver-mapping:${Versions.scylla}"
    const val scylla_driver_extras = "com.scylladb:scylla-driver-extras:${Versions.scylla}"

    // ElasticSearch
    const val elasticsearch_rest_high_level_client =
        "org.elasticsearch.client:elasticsearch-rest-high-level-client:${Versions.elasticsearch}"
    const val elasticsearch_rest_client = "org.elasticsearch.client:elasticsearch-rest-client:${Versions.elasticsearch}"
    const val elasticsearch_rest_client_sniffer =
        "org.elasticsearch.client:elasticsearch-rest-client-sniffer:${Versions.elasticsearch}"

    // InfluxDB
    const val influxdb_java = "org.influxdb:influxdb-java:2.16"
    const val influxdb_spring_data = "com.github.miwurster:spring-data-influxdb:1.8"

    // RabbitMQ
    const val amqp_client = "com.rabbitmq:amqp-client:5.9.0"

    // Kafka
    const val kafka_clients = "org.apache.kafka:kafka-clients:2.6.0"
    const val pulsar_client = "org.apache.pulsar:pulsar-client:2.4.0"

    // Zipkin
    const val zipkin_brave = "io.zipkin.brave:brave:5.6.9"

    // Hashicorp Vault
    const val vault_java_driver = "com.bettercloud:vault-java-driver:5.1.0"

    // Hibernate
    const val hibernate_core = "org.hibernate:hibernate-core:${Versions.hibernate}"
    const val hibernate_jcache = "org.hibernate:hibernate-jcache:${Versions.hibernate}"
    const val hibernate_testing = "org.hibernate:hibernate-testing:${Versions.hibernate}"
    const val hibernate_envers = "org.hibernate:hibernate-envers:${Versions.hibernate}"
    const val javassist = "org.javassist:javassist:3.27.0-GA"

    // Validators
    const val hibernate_validator = "org.hibernate:hibernate-validator:${Versions.hibernate_validator}"
    const val hibernate_validator_annotation_processor =
        "org.hibernate:hibernate-validator-annotation-processor:${Versions.hibernate_validator}"

    // Expression
    const val javax_el = "org.glassfish:javax.el:3.0.1-b11"
    const val javax_el_api = "javax.el:javax.el-api:3.0.0"


    const val querydsl_apt = "com.querydsl:querydsl-apt:${Versions.querydsl}"
    const val querydsl_core = "com.querydsl:querydsl-core:${Versions.querydsl}"
    const val querydsl_jpa = "com.querydsl:querydsl-jpa:${Versions.querydsl}"
    const val querydsl_sql = "com.querydsl:querydsl-sql:${Versions.querydsl}"

    const val hikaricp = "com.zaxxer:HikariCP:3.4.5"
    const val dbcp2 = "org.apache.commons:commons-dbcp2:2.7.0"
    const val tomcat_jdbc = "org.apache.tomcat:tomcat-jdbc:9.0.36"

    const val mysql_connector_java = "mysql:mysql-connector-java:8.0.20"
    const val mariadb_java_client = "org.mariadb.jdbc:mariadb-java-client:2.5.4"
    const val postgresql_driver = "org.postgresql:postgresql:42.2.10"
    const val oracle_ojdbc8 = "com.oracle.ojdbc:ojdbc8:19.3.0.0"

    const val h2 = "com.h2database:h2:1.4.197"
    const val hsqldb = "org.hsqldb:hsqldb:2.5.0"
    const val flyway_core = "org.flywaydb:flyway-core:6.4.3"

    const val exposed = "org.jetbrains.exposed:exposed:0.17.7"

    // UUID Generator
    const val java_uuid_generator = "com.fasterxml.uuid:java-uuid-generator:4.0.1"
    const val uuid_creator = "com.github.f4b6a3:uuid-creator:1.3.9"

    // Cache2K
    const val cache2k_api = "org.cache2k:cache2k-api:${Versions.cache2k}"
    const val cache2k_core = "org.cache2k:cache2k-core:${Versions.cache2k}"
    const val cache2k_jcache = "org.cache2k:cache2k-jcache:${Versions.cache2k}"
    const val cache2k_spring = "org.cache2k:cache2k-spring:${Versions.cache2k}"

    // Caffeine
    const val caffeine = "com.github.ben-manes.caffeine:caffeine:${Versions.caffeine}"
    const val caffeine_guava = "com.github.ben-manes.caffeine:guava:${Versions.caffeine}"
    const val caffeine_jcache = "com.github.ben-manes.caffeine:jcache:${Versions.caffeine}"

    const val ehcache = "org.ehcache:ehcache:3.9.0"

    // Apache Ignite
    const val ignite_aop = "org.apache.ignite:ignite-aop:${Versions.ignite}"
    const val ignite_aws = "org.apache.ignite:ignite-aws:${Versions.ignite}"
    const val ignite_cassandra_store = "org.apache.ignite:ignite-cassandra-store:${Versions.ignite}"
    const val ignite_clients = "org.apache.ignite:ignite-clients:${Versions.ignite}"
    const val ignite_compress = "org.apache.ignite:ignite-compress:${Versions.ignite}"
    const val ignite_core = "org.apache.ignite:ignite-core:${Versions.ignite}"
    const val ignite_direct_io = "org.apache.ignite:ignite-direct-io:${Versions.ignite}"
    const val ignite_hibernate_core = "org.apache.ignite:ignite-hibernate-core:${Versions.ignite}"
    const val ignite_indexing = "org.apache.ignite:ignite-indexing:${Versions.ignite}"
    const val ignite_jta = "org.apache.ignite:ignite-jta:${Versions.ignite}"
    const val ignite_kafka = "org.apache.ignite:ignite-kafka:${Versions.ignite}"
    const val ignite_kubenetes = "org.apache.ignite:ignite-kubenetes:${Versions.ignite}"
    const val ignite_rest_http = "org.apache.ignite:ignite-rest-http:${Versions.ignite}"
    const val ignite_slf4j = "org.apache.ignite:ignite-slf4j:${Versions.ignite}"
    const val ignite_spring = "org.apache.ignite:ignite-spring:${Versions.ignite}"
    const val ignite_spring_data_2_2 = "org.apache.ignite:ignite-spring-data_2_2:${Versions.ignite}"
    const val ignite_tools = "org.apache.ignite:ignite-tools:${Versions.ignite}"
    const val ignite_web = "org.apache.ignite:ignite-web:${Versions.ignite}"
    const val ignite_zookeeper = "org.apache.ignite:ignite-zookeeper:${Versions.ignite}"

    const val hazelcast = "com.hazelcast:hazelcast:${Versions.hazelcast}"
    const val hazelcast_all = "com.hazelcast:hazelcast-all:${Versions.hazelcast}"
    const val hazelcast_client = "com.hazelcast:hazelcast-client:${Versions.hazelcast}"
    const val hazelcast_spring = "com.hazelcast:hazelcast-spring:${Versions.hazelcast}"

    const val koin_core = "org.koin:koin-core:${Versions.koin}"
    const val koin_core_ext = "org.koin:koin-core-ext:${Versions.koin}"
    const val koin_test = "org.koin:koin-test:${Versions.koin}"

    // CSV parsers
    const val univocity_parsers = "com.univocity:univocity-parsers:2.8.4"

    const val objenesis = "org.objenesis:objenesis:3.1"
    const val ow2_asm = "org.ow2.asm:asm:9.0"
    const val ow2_asm_commons = "org.ow2.asm:asm-commons:9.0"
    const val ow2_asm_util = "org.ow2.asm:asm-util:9.0"
    const val ow2_asm_tree = "org.ow2.asm:asm-tree:9.0"

    const val lombok = "org.projectlombok:lombok:1.18.10"

    // junit 5.4+ 부터는 junit-jupiter 만 있으면 됩니다.
    const val junit_jupiter = "org.junit.jupiter:junit-jupiter:${Versions.junit_jupiter}"
    const val junit_jupiter_api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit_jupiter}"
    const val junit_jupiter_engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit_jupiter}"
    const val junit_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit_jupiter}"
    const val junit_jupiter_migrationsupport =
        "org.junit.jupiter:junit-jupiter-migrationsupport:${Versions.junit_jupiter}"

    const val junit_platform_commons = "org.junit.platform:junit-platform-commons:${Versions.junit_platform}"
    const val junit_platform_engine = "org.junit.platform:junit-platform-engine:${Versions.junit_platform}"
    const val junit_platform_runner = "org.junit.platform:junit-platform-runner:${Versions.junit_platform}"
    const val junit_platform_launcher = "org.junit.platform:junit-platform-launcher:${Versions.junit_platform}"
    const val junit_platform_suite_api = "org.junit.platform:junit-platform-suite-api:${Versions.junit_platform}"

    const val kluent = "org.amshove.kluent:kluent:1.61"
    const val assertj_core = "org.assertj:assertj-core:${Versions.assertj_core}"

    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val springmockk = "com.ninja-squad:springmockk:2.0.3"

    const val mockito_core = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockito_junit_jupiter = "org.mockito:mockito-junit-jupiter:${Versions.mockito}"
    const val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
    const val jmock_junit5 = "org.jmock:jmock-junit5:2.12.0"

    const val javafaker = "com.github.javafaker:javafaker:1.0.2"
    const val random_beans = "io.github.benas:random-beans:3.9.0"

    const val mockserver_netty = "org.mock-server:mockserver-netty:5.10.0"
    const val mockserver_client_java = "org.mock-server:mockserver-client-java:5.10.0"

    const val system_rules = "com.github.stefanbirkner:system-rules:1.19.0"

    const val jmh_core = "org.openjdk.jmh:jmh-core:${Versions.jmh}"
    const val jmh_generator_annprocess = "org.openjdk.jmh:jmh-generator-annprocess:${Versions.jmh}"

    const val testcontainers_bom = "org.testcontainers:testcontainers-bom:${Versions.testcontainers}"
    const val testcontainers: String = "org.testcontainers:testcontainers:${Versions.testcontainers}"
    const val testcontainers_junit_jupiter: String = "org.testcontainers:junit-jupiter:${Versions.testcontainers}"
    const val testcontainers_cassandra: String = "org.testcontainers:cassandra:${Versions.testcontainers}"
    const val testcontainers_elasticsearch: String = "org.testcontainers:elasticsearch:${Versions.testcontainers}"
    const val testcontainers_influxdb: String = "org.testcontainers:influxdb:${Versions.testcontainers}"
    const val testcontainers_dynalite = "org.testcontainers:dynalite:${Versions.testcontainers}"
    const val testcontainers_mariadb: String = "org.testcontainers:mariadb:${Versions.testcontainers}"
    const val testcontainers_mysql: String = "org.testcontainers:mysql:${Versions.testcontainers}"
    const val testcontainers_postgresql: String = "org.testcontainers:postgresql:${Versions.testcontainers}"
    const val testcontainers_oracle_xe = "org.testcontainers:oracle-xe:${Versions.testcontainers}"
    const val testcontainers_kafka = "org.testcontainers:kafka:${Versions.testcontainers}"
    const val testcontainers_pulsar = "org.testcontainers:pulsar:${Versions.testcontainers}"
    const val testcontainers_rabbitmq = "org.testcontainers:rabbitmq:${Versions.testcontainers}"
    const val testcontainers_vault = "org.testcontainers:vault:${Versions.testcontainers}"

    // the Atlassian's LocalStack, 'a fully functional local AWS cloud stack'.
    const val testcontainers_localstack = "org.testcontainers:localstack:${Versions.testcontainers}"
    const val testcontainers_mockserver = "org.testcontainers:mockserver:${Versions.testcontainers}"

    const val jna = "net.java.dev.jna:jna:5.8.0"

    // Twitter Text
    // 한글 분석을 위한 기본적인 통계 정보를 Twitter에서 제공합니다 
    const val twitter_text = "com.twitter.twittertext:twitter-text:3.1.0"
    const val open_korean_text = "org.openkoreantext:open-korean-text:2.3.1"

}
