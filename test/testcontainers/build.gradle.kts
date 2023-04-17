configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-io-core"))
    testImplementation(project(":bluetape4k-test-junit5"))


    api(Libs.testcontainers)
    compileOnly(Libs.testcontainers_junit_jupiter)
    // Apple Silicon에서 testcontainers 를 사용하기 위해 참조해야 합니다.
    api(Libs.jna)
    api(Libs.jna_platform)

    // Databases
    compileOnly(Libs.testcontainers_mysql)
    compileOnly(Libs.testcontainers_mariadb)
    compileOnly(Libs.testcontainers_postgresql)

    compileOnly(Libs.hikaricp)

    testImplementation(Libs.mysql_connector_java)
    testImplementation(Libs.mariadb_java_client)
    testImplementation(Libs.postgresql_driver)

    // Redis
    compileOnly(Libs.redisson)
    compileOnly(Libs.lettuce_core)

    compileOnly(Libs.fst)
    compileOnly(Libs.snappy_java)

    // Hazelcast
    compileOnly(Libs.hazelcast_all)
    compileOnly(Libs.hazelcast_client)

    // MongoDB
    compileOnly(Libs.testcontainers_mongodb)
    compileOnly(Libs.mongodb_driver_sync)

    // Kafka
    compileOnly(Libs.testcontainers_kafka)
    testImplementation(Libs.kafka_clients)

    // RabbitMQ
    compileOnly(Libs.testcontainers_rabbitmq)
    testImplementation(Libs.amqp_client)

    // Zipkin
    testImplementation(Libs.zipkin_brave)

    // HashiCorp Vault
    compileOnly(Libs.testcontainers_vault)
    testImplementation(Libs.vault_java_driver)

    // OkHttp
    testImplementation(Libs.okhttp3)

    // LocalStack for AWS
    compileOnly(Libs.testcontainers_localstack)

    // Amazon SDK V2
    compileOnly(Libs.aws2_auth)
    testImplementation(Libs.aws2_cloudwatch)
    testImplementation(Libs.aws2_cloudwatchevents)
    testImplementation(Libs.aws2_cloudwatchlogs)
    testImplementation(Libs.aws2_dynamodb_enhanced)
    testImplementation(Libs.aws2_kms)
    testImplementation(Libs.aws2_s3)
    testImplementation(Libs.aws2_ses)
    testImplementation(Libs.aws2_sqs)

    testImplementation(Libs.metrics_jmx)
    testImplementation(Libs.netty_transport_native_epoll + ":linux-x86_64")
    testImplementation(Libs.netty_transport_native_kqueue + ":osx-x86_64")

    // ElasticSearch
    compileOnly(Libs.testcontainers_elasticsearch)
    testImplementation(Libs.elasticsearch_rest_client)
    testImplementation(Libs.elasticsearch_rest_high_level_client)

    // K3s
    compileOnly(Libs.testcontainers_k3s)

    testImplementation(Libs.fabric8_kubernetes_client)
    testImplementation(Libs.kubernetes_client_java)

    testImplementation(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)
}
