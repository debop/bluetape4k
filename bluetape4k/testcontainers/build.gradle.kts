configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.awaitility_kotlin)

    api(Libs.testcontainers)
    compileOnly(Libs.testcontainers_junit_jupiter)
    // Apple Silicon에서 testcontainers 를 사용하기 위해 참조해야 합니다.
    api(Libs.jna)
    api(Libs.jna_platform)

    // Databases
    compileOnly(Libs.testcontainers_mysql)
    compileOnly(Libs.testcontainers_mariadb)
    compileOnly(Libs.testcontainers_postgresql)
    compileOnly(Libs.testcontainers_cockroachdb)

    compileOnly(Libs.hikaricp)

    testImplementation(Libs.mysql_connector_j)
    testImplementation(Libs.mariadb_java_client)
    testImplementation(Libs.postgresql_driver)

    // R2DBC
    compileOnly(Libs.testcontainers_r2dbc)
    compileOnly(Libs.r2dbc_mysql_0_9)
    compileOnly(Libs.r2dbc_mariadb)
    compileOnly(Libs.r2dbc_postgresql)
    compileOnly(Libs.springBootStarter("data-r2dbc"))

    // Milvus
    compileOnly(Libs.milvus_sdk_java)

    // Redis
    compileOnly(Libs.redisson)
    compileOnly(Libs.lettuce_core)

    compileOnly(Libs.kryo)
    compileOnly(Libs.marshalling)
    compileOnly(Libs.marshalling_river)

    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)

    // Hazelcast
    compileOnly(Libs.hazelcast)

    // MongoDB
    compileOnly(Libs.testcontainers_mongodb)
    compileOnly(Libs.mongodb_driver_sync)

    // Cassandra
    compileOnly(Libs.testcontainers_cassandra)
    compileOnly(Libs.cassandra_java_driver_core)
    compileOnly(Libs.cassandra_java_driver_query_builder)

    // ElasticSearch
    compileOnly(Libs.testcontainers_elasticsearch)
    compileOnly(Libs.elasticsearch_rest_client)
    compileOnly(Libs.springData("elasticsearch"))

    // Kafka
    compileOnly(Libs.testcontainers_kafka)
    compileOnly(Libs.kafka_clients)
    compileOnly(Libs.spring_kafka)

    // Pulsar
    compileOnly(Libs.testcontainers_pulsar)
    compileOnly(Libs.pulsar_client)

    // Redpanda
    compileOnly(Libs.testcontainers_redpanda)

    // NATS
    compileOnly(Libs.jnats)

    // RabbitMQ
    compileOnly(Libs.testcontainers_rabbitmq)
    testImplementation(Libs.amqp_client)

    // Zipkin
    testImplementation(Libs.zipkin_brave)

    // HashiCorp Vault
    compileOnly(Libs.testcontainers_vault)
    compileOnly(Libs.vault_java_driver)

    // OkHttp
    testImplementation(Libs.okhttp3)

    // LocalStack for AWS
    compileOnly(Libs.testcontainers_localstack)

    // Amazon SDK V2
    compileOnly(Libs.aws2_auth)
    testImplementation(Libs.aws2_aws_core)
    testImplementation(Libs.aws2_sdk_core)
    testImplementation(Libs.aws2_apache_client)
    testImplementation(Libs.aws2_cloudwatch)
    testImplementation(Libs.aws2_cloudwatchevents)
    testImplementation(Libs.aws2_cloudwatchlogs)
    testImplementation(Libs.aws2_dynamodb_enhanced)
    testImplementation(Libs.aws2_kms)
    testImplementation(Libs.aws2_s3)
    testImplementation(Libs.aws2_ses)
    testImplementation(Libs.aws2_sqs)

    // https://docs.aws.amazon.com/ko_kr/sdk-for-java/latest/developer-guide/http-configuration-crt.html
    // https://mvnrepository.com/artifact/software.amazon.awssdk.crt/aws-crt
    testImplementation(Libs.aws2_aws_crt)

    testImplementation(Libs.metrics_jmx)

    // K3s
    compileOnly(Libs.testcontainers_k3s)
    testImplementation(Libs.fabric8_kubernetes_client)
    testImplementation(Libs.kubernetes_client_java)

    // Minio
    compileOnly(Libs.minio)
    compileOnly(Libs.testcontainers_minio)

    // Immudb
    compileOnly(Libs.immudb4j)

    // Curator framework for Zookeeper
    compileOnly(Libs.curator_framework)

    // Ollama
    compileOnly(Libs.testcontainers_ollama)
    testImplementation(Libs.rest_assured)
    testImplementation(Libs.rest_assured_kotlin)

    // Nginx
    compileOnly(Libs.testcontainers_nginx)

    // ClickHouse
    compileOnly(Libs.testcontainers_clickhouse)
    testImplementation(Libs.clickhouse_jdbc)
    testImplementation(Libs.httpclient5)

    // Weaviate
    compileOnly(Libs.testcontainers_weaviate)
    compileOnly(Libs.weaviate_client)

    // ChromaDB
    compileOnly(Libs.testcontainers_chromadb)

    // TiDB
    compileOnly(Libs.testcontainers_tidb)
    testImplementation(Libs.mysql_connector_j)

    testImplementation(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(Libs.netty_all)
}
